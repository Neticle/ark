// Copyright 2018 Igor Azevedo <igor.azevedo@neticle.pt>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pt.neticle.ark.data;

import pt.neticle.ark.functional.ThrowingSupplier;

import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

public class MediaType
{
    public static class Application extends MediaType
    {
        /**
         * For binary data without specific or known subtype, application/octet-stream should be used.
         */
        public static Application OCTET_STREAM = new Application("octet-stream");
        public static Application JSON = new Application("json");
        public static Application PDF = new Application("pdf");
        public static Application XML = new Application("xml");
        public static Application X_WWW_FORM_URLENCODED = new Application("x-www-form-urlencoded");

        public Application (String subtype)
        {
            super("application", subtype);
        }
    }

    public static class Text extends MediaType
    {
        public static Text PLAIN = new Text("plain");
        public static Text HTML = new Text("html");
        public static Text CSS = new Text("css");
        public static Text JAVASCRIPT = new Text("javascript");

        public Text (String subtype)
        {
            super("text", subtype);
        }
    }

    public static class Image extends MediaType
    {
        public static Image GIF = new Image("gif");
        public static Image JPEG = new Image("jpeg");
        public static Image PNG = new Image("png");

        public Image (String subtype)
        {
            super("image", subtype);
        }
    }

    public static class Multipart extends MediaType
    {
        public static Multipart FORM_DATA = new Multipart("form-data");

        public Multipart (String subtype)
        {
            super("multipart", subtype);
        }
    }

    public static class Incomplete extends MediaType
    {
        private static String extractTypeName (String fullname) throws ParseException
        {
            if(!fullname.matches("^([\\w-]+)\\/([\\w-]+)$"))
            {
                throw new ParseException("Invalid media type format", fullname.length());
            }

            return fullname.split("\\/")[0];
        }

        private static String extractSubtypeName (String fullname) throws ParseException
        {
            if(!fullname.matches("^([\\w-]+)\\/([\\w-]+)$"))
            {
                throw new ParseException("Invalid media type format", fullname.length());
            }

            return fullname.split("\\/")[1];
        }

        public Incomplete (String fullname) throws ParseException
        {
            this(extractTypeName(fullname), extractSubtypeName(fullname));
        }

        public Incomplete (String type, String subtype)
        {
            super(type, subtype);
        }

        @Override
        public boolean isKnown ()
        {
            return false;
        }
    }

    private static final ConcurrentHashMap<String, MediaType> types;

    static
    {
        types = new ConcurrentHashMap<>();
    }

    private static MediaType register (MediaType mediaType)
    {
        types.put(mediaType.toString(), mediaType);
        return mediaType;
    }

    public static MediaType valueOf (String name)
    {
        return types.get(name);
    }

    public static MediaType valueOfOr (String name, ThrowingSupplier<MediaType, ParseException> orElseOther) throws ParseException
    {
        return types.containsKey(name) ? types.get(name) : orElseOther.get();
    }

    private final String type;
    private final String subtype;

    private MediaType (String type, String subtype)
    {
        this.type = type;
        this.subtype = subtype;

        register(this);
    }

    public String getType ()
    {
        return type;
    }

    public String getSubtype ()
    {
        return subtype;
    }

    @Override
    public String toString ()
    {
        return getType() + '/' + getSubtype();
    }

    public boolean isKnown ()
    {
        return true;
    }
}
