package tools.jackson.jaxrs.json.testutil;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

public final class NoCheckSubTypeValidator
    extends PolymorphicTypeValidator.Base
{
    private static final long serialVersionUID = 1L;

    public final static NoCheckSubTypeValidator instance = new NoCheckSubTypeValidator(); 

    @Override
    public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
        return Validity.ALLOWED;
    }
}
