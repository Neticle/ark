package pt.neticle.ark.introspection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArkTypeUtils
{
    public static String getPackageName (Class klass)
    {
        return klass.getPackage().getName();
    }

    public static String getPackageName (String typename)
    {
        String pkg = typename.substring(0, typename.lastIndexOf('.'));

        return Package.getPackage(pkg) != null ? pkg : null;
    }

    public static Optional<ParametersList> parseMethodParametersSignature (String signature)
    {
        Pattern parametersRx = Pattern.compile("\\((.+)?\\)$");
        Matcher parameterMatcher = parametersRx.matcher(signature);

        if(!parameterMatcher.find())
        {
            return Optional.empty();
        }

        return Optional.of(new ParametersList(parameterMatcher.group(1)));
    }

    public static Optional<ParameterType> parseMethodReturnType (String signature)
    {
        Pattern signatureRx = Pattern.compile("(.+)\\s?(?:\\((.*)\\))$");
        Matcher signatureMatcher = signatureRx.matcher(signature);

        if(!signatureMatcher.find())
        {
            return Optional.empty();
        }

        String[] signatureParts = signatureMatcher.group(1)
            .split(" ");

        if(signatureParts.length < 2)
        {
            return Optional.empty();
        }

        return Optional.of(new ParameterType(signatureParts[signatureParts.length-2]));
    }

    public static class ParametersList
    {
        private final List<ParameterType> parameters;

        public ParametersList (String signature)
        {
            List<String> parameters = new ArrayList<>();

            if(signature != null && signature.length() > 0)
            {
                signature += ',';
                int startIndex = 0;
                int inGenericLevel = 0;
                for(int i = 0; i < signature.length(); i++)
                {
                    char c = signature.charAt(i);

                    if(inGenericLevel == 0 && c == ',')
                    {
                        inGenericLevel = 0;
                        parameters.add(signature.substring(startIndex, i).trim());
                        startIndex = i + 1;
                        continue;
                    }

                    if(c == '<')
                    {
                        inGenericLevel++;
                        continue;
                    }

                    if(c == '>')
                    {
                        inGenericLevel--;
                        continue;
                    }
                }
            }

            this.parameters = parameters.stream()
                .map((param) -> new ParameterType(param))
                .collect(Collectors.toList());
        }

        public List<ParameterType> getParameters ()
        {
            return parameters;
        }

        public Stream<ParameterType> parameters ()
        {
            return parameters.stream();
        }

        public void resolveTypes () throws ClassNotFoundException
        {
            for(ParameterType pt : parameters)
            {
                pt.resolveType();
            }
        }
    }

    public static class ParameterType
    {
        private ParametersList genericTypes = null;
        private final String typeName;
        private Class type;

        public ParameterType (String signature)
        {
            Pattern genericRx = Pattern.compile("<(.+)>$");
            Matcher genericMatcher = genericRx.matcher(signature);

            if(genericMatcher.find())
            {
                this.genericTypes = new ParametersList(genericMatcher.group(1));
                this.typeName = signature.substring(0, signature.indexOf('<'));
            }
            else
            {
                this.typeName = signature;
            }
        }

        public String getTypeName ()
        {
            return typeName;
        }

        public Class<? extends Object> getType ()
        {
            return type;
        }

        public Optional<ParametersList> getParametersList ()
        {
            return Optional.ofNullable(genericTypes);
        }

        public Stream<ParameterType> parameters ()
        {
            return genericTypes == null ? Stream.empty() : genericTypes.parameters();
        }

        private ParameterType parameterAt0 (int i)
        {
            return genericTypes != null ? genericTypes.getParameters().get(i) : null;
        }

        public Optional<ParameterType> parameterAt (int i)
        {
            return Optional.ofNullable(parameterAt0(i));
        }

        public void resolveType () throws ClassNotFoundException
        {
            if(typeName.equals("?"))
            {
                return;
            }

            type = Class.forName(typeName);

            if(genericTypes != null)
            {
                genericTypes.resolveTypes();
            }
        }

        @Override
        public String toString ()
        {
            String name = typeName;

            if(genericTypes != null)
            {
                name += '<' + genericTypes.parameters().map(ParameterType::toString).collect(Collectors.joining(",")) + '>';
            }

            return name;
        }
    }
}
