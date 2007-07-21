package gldapwrap.mapping;
import org.springframework.ldap.core.AttributesMapper;
import javax.naming.directory.Attributes;

public interface GldapwrapAttributeMapper extends AttributesMapper
{
	public void setMapToClass(Class clazz);
}