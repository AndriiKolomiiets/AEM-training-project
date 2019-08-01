package trainingproject.core.filters;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface OutputFilterConfig {

    @AttributeDefinition(name = "Turn Upside Down", description = "Turns images upside-down", type = AttributeType.BOOLEAN)
    boolean turnUpDown () default true;

    @AttributeDefinition(name = "Gray Scale", description = "Turns images upside-down", type = AttributeType.BOOLEAN)
    boolean grayScale () default true;
}
