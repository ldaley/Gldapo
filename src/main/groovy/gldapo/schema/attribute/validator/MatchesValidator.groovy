package gldapo.schema.attribute.validator
import java.util.regex.Pattern

/**
 * Ensures that the string representation of each value of the attribute matches a pattern.
 */
class MatchesValidator extends AbstractAttributeValidator {
    
    def pattern
    def errorCodes = ["matches.invalid"]
    
    /**
     * 
     */
    void init() throws Exception {
        if (config.pattern == null) throw new IllegalStateException("pattern is null")
        pattern = Pattern.compile(config.pattern)
        def label = config.label
        if (label) errorCodes << "${errorCodes.head()}.$label"
    }
    
    /**
     */
    def validateValue(obj) {
        (pattern.matcher(obj.toString()).matches()) ? null : errorCodes
    }
    
}