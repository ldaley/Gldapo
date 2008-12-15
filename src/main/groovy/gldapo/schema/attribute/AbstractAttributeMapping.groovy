/* 
 * Copyright 2007 Luke Daley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gldapo.schema.attribute
import org.apache.commons.lang.WordUtils
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import gldapo.exception.GldapoTypeMappingException
import gldapo.exception.GldapoException
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoPseudoType
import gldapo.GldapoTypeMappingRegistry
import gldapo.Gldapo
import org.apache.commons.lang.StringUtils
import gldapo.schema.attribute.validator.AttributeValidator
import gldapo.schema.constraint.ConstraintAnnotationPropertyInspector
import gldapo.schema.constraint.InvalidConstraintException

/**
 * Represents the bridging between the data of the LDAP world and the Groovy world
 * 
 * property - Groovy side
 * attribute - LDAP side
 */
abstract class AbstractAttributeMapping implements AttributeMapping
{
    
    /**
     * 
     */
    Class schema
    
    /**
     * 
     */
    Field field
    
    /**
     * The name on the LDAP side
     */
    String attributeName

    /**
     * pseudo property type
     */
    String typeMapping
    
    /**
     * 
     */
    Closure toGroovyTypeMapper
    
    /**
     *
     */
     Closure toLdapTypeMapper
     
     /**
      * 
      */
     Collection<AttributeValidator> validators
     
    /**
     * 
     */
    abstract protected calculateTypeMappingFromFieldType()

    /**
     * 
     */
    abstract protected getGroovyValueFromContext(context)

    /**
     * 
     */
    abstract protected calculateModificationItems(clean, dirty)
     
     /**
      * 
      */
     abstract protected getAttribute(obj)
     
    /**
     * 
     */
    AbstractAttributeMapping(Class schema, Field field, Gldapo gldapo) {
        this.schema = schema
        this.field = field
        
        this.attributeName = this.calculateAttributeName()
        this.typeMapping = this.calculateTypeMapping()
        this.toGroovyTypeMapper = this.calculateToGroovyTypeMapper(gldapo.typemappings)
        this.toLdapTypeMapper = this.calculateToLdapTypeMapper(gldapo.typemappings)
        this.validators = this.calculateValidators(gldapo.constraintTypes)
    }
    
    protected calculateAttributeName() {
        def synonymAnnotation = this.field.getAnnotation(GldapoSynonymFor)
        (synonymAnnotation) ? synonymAnnotation.value() : this.field.name
    }
    
    protected calculateTypeMapping() {
        def pseudoTypeAnnotation = this.field.getAnnotation(GldapoPseudoType)
        if (pseudoTypeAnnotation) {
            return pseudoTypeAnnotation.value()
        } else {
            return this.calculateTypeMappingFromFieldType()
        }
    }
    
    /**
     * 
     */    
    protected calculateToGroovyTypeMapper(GldapoTypeMappingRegistry typemappings) {
        Class[] p = [Object] as Class[]
        
        def byFieldMapperName = toGroovyByFieldMapperName(this.field.name)
        def classByFieldMapper = schema.metaClass.getMetaMethod(byFieldMapperName, p)
        if (classByFieldMapper) return { classByFieldMapper.invoke(schema, (it.class.array) ? [it] as Object[] : it) }
        
        def byTypeMapperName = toGroovyByTypeMapperName(this.typeMapping)
        def classByTypeMapper = schema.metaClass.getMetaMethod(byTypeMapperName, p)
        if (classByTypeMapper) return { classByTypeMapper.invoke(schema, (it.class.array) ? [it] as Object[] : it) }
        
        def defaultByTypeMapper = typemappings.getToGroovyMapperForType(this.typeMapping)
        if (defaultByTypeMapper) return defaultByTypeMapper

        throw new GldapoTypeMappingException(this.schema, this.field.name, this.typeMapping, GldapoTypeMappingException.MAPPING_TO_FIELD, "No available type mapping")
    }
    
    /**
     * 
     */    
    protected calculateToLdapTypeMapper(GldapoTypeMappingRegistry typemappings) {
        Class[] p = [Object] as Class[]
        
        def byFieldMapperName = toLdapByTypeMapperName(this.field.name)
        def classByFieldMapper = schema.metaClass.getMetaMethod(byFieldMapperName, p)
        if (classByFieldMapper) return { classByFieldMapper.invoke(schema, (it.class.array) ? [it] as Object[] : it) }
        
        def byTypeMapperName = toGroovyByTypeMapperName(this.typeMapping)
        def classByTypeMapper = schema.metaClass.getMetaMethod(byTypeMapperName, p)
        if (classByTypeMapper) return { classByTypeMapper.invoke(schema, (it.class.array) ? [it] as Object[] : it) }
        
        def defaultByTypeMapper = typemappings.getToLdapMapperForType(this.typeMapping)
        if (defaultByTypeMapper) return defaultByTypeMapper

        throw new GldapoTypeMappingException(this.schema, this.field.name, this.typeMapping, GldapoTypeMappingException.MAPPING_FROM_FIELD, "No available type mapping")
    }

    def calculateValidators(constraintTypes) {
        def validators = []
        this.field.annotations.each {
            def validatorType = constraintTypes[it.annotationType()]
            if (validatorType) {
                try {
                    def validator = validatorType.newInstance(config: ConstraintAnnotationPropertyInspector.inspect(it), attributeMapping: this)
                    validator.init()
                    validators << validator
                }
                catch(Exception e) {
                    throw new InvalidConstraintException("Constraint '${it.annotationType().simpleName}' of attribute '${field.name}' of class '${schema.simpleName}' is invalid", e)
                }
            }
        }
        validators
    }
    
    /**
     * 
     */
    def mapToGroovyType(s) {
        this.toGroovyTypeMapper.call(s)
    }

    /**
     * 
     */
    def mapToLdapType(s) {
        this.toLdapTypeMapper.call(s)
    }

    /**
     * 
     */
    def mapFromContext(context, subject) {
        try {
            subject."${this.field.name}" = this.getGroovyValueFromContext(context)
        } catch (Exception cause) {
            throw new GldapoTypeMappingException(this.schema, this.field.name, this.typeMapping, GldapoTypeMappingException.MAPPING_TO_FIELD, cause)
        }
    }
    
    /**
     * 
     */
    static toGroovyByFieldMapperName(String fieldName) {
        "mapTo" + WordUtils.capitalize(fieldName) + "Field"
    }

    /**
     * 
     */
    static toGroovyByTypeMapperName(String typeName) {
        "mapTo" + typeName + "Type"
    }

    /**
     * 
     */    
    static toLdapByFieldMapperName(String fieldName) {
        "mapFrom" + WordUtils.capitalize(fieldName) + "Field"
    }

    /**
     * 
     */
    static toLdapByTypeMapperName(String typeName) {
        "mapFrom" + typeName + "Type"
    }

    protected typeNameFromClass(clazz) {
        def name 
        if (clazz instanceof GenericArrayType) {
            name = clazz.genericComponentType.simpleName + "Array"
        } else {
            name = clazz.simpleName
            if (clazz.array) {
                name = name.substring(0, name.size() - 2) + "Array" 
            }
        }
        name
    }
}