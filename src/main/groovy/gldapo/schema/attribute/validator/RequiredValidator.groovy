package gldapo.schema.attribute.validator

/**
 * Ensures that a field has a value.
 * 
 * @see #validate(Object)
 */
class RequiredValidator extends AbstractFieldValidator {
    
    /**
     * Validates that single value attributes are not {@code null} and that
     * multi-value attributes have at least one value.
     * 
     * The error code on failure is '{@code required}'.
     */
    def validate(obj) {
        if (obj == null || (obj instanceof Collection && obj.empty))
            return "required"
    }
    
}