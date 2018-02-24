package pt.neticle.ark.data;

import java.math.BigDecimal;
import java.util.Optional;

public class DefaultConverter extends Converter
{
    public DefaultConverter ()
    {
        super();

        this.addConverter(String.class, Integer.class, (sourceStr) ->
        {
            try {
                return Optional.of(Integer.valueOf(sourceStr));
            } catch (NumberFormatException | NullPointerException e) {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, Double.class, (sourceStr) ->
        {
            try {
                return Optional.of(Double.valueOf(sourceStr));
            } catch(NumberFormatException | NullPointerException e)
            {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, BigDecimal.class, (sourceStr) ->
        {
            try {
                return Optional.of(new BigDecimal(sourceStr));
            } catch(NumberFormatException | NullPointerException e)
            {
                return Optional.empty();
            }
        });

        this.addConverter(String.class, Boolean.class,
            (sourceStr) -> Optional.of(sourceStr.equalsIgnoreCase("true") || sourceStr.equals("1")));
    }
}
