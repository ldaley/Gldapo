package gldapo.schema.attribute.validator

/**
 * Ensures that an attribute has at least one value.
 */
class RequiredValidator extends AbstractAttributeValidator {
    
    final skipNull = false
    final skipEmpty = false
    
    /**
     * @return {@code null} if {@code obj} is non {@code null} and non empty, otherwise an error code of "required".
     */
    protected validateMultiValue(Collection obj) {
        (obj == null || obj.empty) ? "required" : null
    }
    
    /**
     * @return {@code null} if {@code obj} is non {@code null}, otherwise an error code of "required".
     */
    protected validateSingleValue(obj) {
        (obj == null) ? "required" : null
    }
    
}