/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
package org.json;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.util.Iterator;

import pt.iflow.applet.Messages;


/**
 * This provides static methods to convert an XML text into a JSONObject,
 * and to covert a JSONObject into an XML text.
 * @author JSON.org
 * @version 2008-10-14
 */
public class XML {

    /** The Character '&'. */
    public static final Character AMP   = new Character('&');

    /** The Character '''. */
    public static final Character APOS  = new Character('\'');

    /** The Character '!'. */
    public static final Character BANG  = new Character('!');

    /** The Character '='. */
    public static final Character EQ    = new Character('=');

    /** The Character '>'. */
    public static final Character GT    = new Character('>');

    /** The Character '<'. */
    public static final Character LT    = new Character('<');

    /** The Character '?'. */
    public static final Character QUEST = new Character('?');

    /** The Character '"'. */
    public static final Character QUOT  = new Character('"');

    /** The Character '/'. */
    public static final Character SLASH = new Character('/');

    /**
     * Replace special characters with XML escapes:
     * <pre>
     * &amp; <small>(ampersand)</small> is replaced by &amp;amp;
     * &lt; <small>(less than)</small> is replaced by &amp;lt;
     * &gt; <small>(greater than)</small> is replaced by &amp;gt;
     * &quot; <small>(double quote)</small> is replaced by &amp;quot;
     * </pre>
     * @param string The string to be escaped.
     * @return The escaped string.
     */
    public static String escape(String string) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, len = string.length(); i < len; i++) {
            char c = string.charAt(i);
            switch (c) {
            case '&':
                sb.append("&amp;"); //$NON-NLS-1$
                break;
            case '<':
                sb.append("&lt;"); //$NON-NLS-1$
                break;
            case '>':
                sb.append("&gt;"); //$NON-NLS-1$
                break;
            case '"':
                sb.append("&quot;"); //$NON-NLS-1$
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * Throw an exception if the string contains whitespace. 
     * Whitespace is not allowed in tagNames and attributes.
     * @param string
     * @throws JSONException
     */
    public static void noSpace(String string) throws JSONException {
    	int i, length = string.length();
    	if (length == 0) {
    		throw new JSONException("Empty string."); //$NON-NLS-1$
    	}
    	for (i = 0; i < length; i += 1) {
		    if (Character.isWhitespace(string.charAt(i))) {
		    	throw new JSONException("'" + string +  //$NON-NLS-1$
		    			"' contains a space character."); //$NON-NLS-1$
		    }
		}
    }

    /**
     * Scan the content following the named tag, attaching it to the context.
     * @param x       The XMLTokener containing the source string.
     * @param context The JSONObject that will include the new material.
     * @param name    The tag name.
     * @return true if the close tag is processed.
     * @throws JSONException
     */
    private static boolean parse(XMLTokener x, JSONObject context,
                                 String name) throws JSONException {
        char       c;
        int        i;
        String     n;
        JSONObject o = null;
        String     s;
        Object     t;

// Test for and skip past these forms:
//      <!-- ... -->
//      <!   ...   >
//      <![  ... ]]>
//      <?   ...  ?>
// Report errors for these forms:
//      <>
//      <=
//      <<

        t = x.nextToken();

// <!

        if (t == BANG) {
            c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->"); //$NON-NLS-1$
                    return false;
                }
                x.back();
            } else if (c == '[') {
                t = x.nextToken();
                if (t.equals("CDATA")) { //$NON-NLS-1$
                    if (x.next() == '[') {
                        s = x.nextCDATA();
                        if (s.length() > 0) {
                            context.accumulate("content", s); //$NON-NLS-1$
                        }
                        return false;
                    }
                }
                throw x.syntaxError("Expected 'CDATA['"); //$NON-NLS-1$
            }
            i = 1;
            do {
                t = x.nextMeta();
                if (t == null) {
                    throw x.syntaxError("Missing '>' after '<!'."); //$NON-NLS-1$
                } else if (t == LT) {
                    i += 1;
                } else if (t == GT) {
                    i -= 1;
                }
            } while (i > 0);
            return false;
        } else if (t == QUEST) {

// <?

            x.skipPast("?>"); //$NON-NLS-1$
            return false;
        } else if (t == SLASH) {

// Close tag </

        	t = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag" + t); //$NON-NLS-1$
            }            
            if (!t.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + t); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (x.nextToken() != GT) {
                throw x.syntaxError("Misshaped close tag"); //$NON-NLS-1$
            }
            return true;

        } else if (t instanceof Character) {
            throw x.syntaxError("Misshaped tag"); //$NON-NLS-1$

// Open tag <

        } else {
            n = (String)t;
            t = null;
            o = new JSONObject();
            for (;;) {
                if (t == null) {
                    t = x.nextToken();
                }

// attribute = value

                if (t instanceof String) {
                    s = (String)t;
                    t = x.nextToken();
                    if (t == EQ) {
                        t = x.nextToken();
                        if (!(t instanceof String)) {
                            throw x.syntaxError("Missing value"); //$NON-NLS-1$
                        }
                        o.accumulate(s, JSONObject.stringToValue((String)t));
                        t = null;
                    } else {
                        o.accumulate(s, ""); //$NON-NLS-1$
                    }

// Empty tag <.../>

                } else if (t == SLASH) {
                    if (x.nextToken() != GT) {
                        throw x.syntaxError("Misshaped tag"); //$NON-NLS-1$
                    }
                    context.accumulate(n, o);
                    return false;

// Content, between <...> and </...>

                } else if (t == GT) {
                    for (;;) {
                        t = x.nextContent();
                        if (t == null) {
                            if (n != null) {
                                throw x.syntaxError("Unclosed tag " + n); //$NON-NLS-1$
                            }
                            return false;
                        } else if (t instanceof String) {
                            s = (String)t;
                            if (s.length() > 0) {
                                o.accumulate("content", JSONObject.stringToValue(s)); //$NON-NLS-1$
                            }

// Nested element

                        } else if (t == LT) {
                            if (parse(x, o, n)) {
                                if (o.length() == 0) {
                                    context.accumulate(n, ""); //$NON-NLS-1$
                                } else if (o.length() == 1 &&
                                       o.opt("content") != null) { //$NON-NLS-1$
                                    context.accumulate(n, o.opt("content")); //$NON-NLS-1$
                                } else {
                                    context.accumulate(n, o);
                                }
                                return false;
                            }
                        }
                    }
                } else {
                    throw x.syntaxError("Misshaped tag"); //$NON-NLS-1$
                }
            }
        }
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject. Some information may be lost in this transformation
     * because JSON is a data format and XML is a document format. XML uses
     * elements, attributes, and content text, while JSON uses unordered
     * collections of name/value pairs and arrays of values. JSON does not
     * does not like to distinguish between elements and attributes.
     * Sequences of similar elements are represented as JSONArrays. Content
     * text may be placed in a "content" member. Comments, prologs, DTDs, and
     * <code>&lt;[ [ ]]></code> are ignored.
     * @param string The source string.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject o = new JSONObject();
        XMLTokener x = new XMLTokener(string);
        while (x.more() && x.skipPast("<")) { //$NON-NLS-1$
            parse(x, o, null);
        }
        return o;
    }


    /**
     * Convert a JSONObject into a well-formed, element-normal XML string.
     * @param o A JSONObject.
     * @return  A string.
     * @throws  JSONException
     */
    public static String toString(Object o) throws JSONException {
        return toString(o, null);
    }


    /**
     * Convert a JSONObject into a well-formed, element-normal XML string.
     * @param o A JSONObject.
     * @param tagName The optional name of the enclosing tag.
     * @return A string.
     * @throws JSONException
     */
    public static String toString(Object o, String tagName)
            throws JSONException {
        StringBuffer b = new StringBuffer();
        int          i;
        JSONArray    ja;
        JSONObject   jo;
        String       k;
        Iterator     keys;
        int          len;
        String       s;
        Object       v;
        if (o instanceof JSONObject) {

// Emit <tagName>

            if (tagName != null) {
                b.append('<');
                b.append(tagName);
                b.append('>');
            }

// Loop thru the keys.

            jo = (JSONObject)o;
            keys = jo.keys();
            while (keys.hasNext()) {
                k = keys.next().toString();
                v = jo.opt(k);
                if (v == null) {
                	v = ""; //$NON-NLS-1$
                }
                if (v instanceof String) {
                    s = (String)v;
                } else {
                    s = null;
                }

// Emit content in body

                if (k.equals("content")) { //$NON-NLS-1$
                    if (v instanceof JSONArray) {
                        ja = (JSONArray)v;
                        len = ja.length();
                        for (i = 0; i < len; i += 1) {
                            if (i > 0) {
                                b.append('\n');
                            }
                            b.append(escape(ja.get(i).toString()));
                        }
                    } else {
                        b.append(escape(v.toString()));
                    }

// Emit an array of similar keys

                } else if (v instanceof JSONArray) {
                    ja = (JSONArray)v;
                    len = ja.length();
                    for (i = 0; i < len; i += 1) {
                    	v = ja.get(i);
                    	if (v instanceof JSONArray) {
                            b.append('<');
                            b.append(k);
                            b.append('>');
                    		b.append(toString(v));
                            b.append("</"); //$NON-NLS-1$
                            b.append(k);
                            b.append('>');
                    	} else {
                    		b.append(toString(v, k));
                    	}
                    }
                } else if (v.equals("")) { //$NON-NLS-1$
                    b.append('<');
                    b.append(k);
                    b.append("/>"); //$NON-NLS-1$

// Emit a new tag <k>

                } else {
                    b.append(toString(v, k));
                }
            }
            if (tagName != null) {

// Emit the </tagname> close tag

                b.append("</"); //$NON-NLS-1$
                b.append(tagName);
                b.append('>');
            }
            return b.toString();

// XML does not have good support for arrays. If an array appears in a place
// where XML is lacking, synthesize an <array> element.

        } else if (o instanceof JSONArray) {
            ja = (JSONArray)o;
            len = ja.length();
            for (i = 0; i < len; ++i) {
            	v = ja.opt(i);
                b.append(toString(v, (tagName == null) ? "array" : tagName)); //$NON-NLS-1$
            }
            return b.toString();
        } else {
            s = (o == null) ? "null" : escape(o.toString()); //$NON-NLS-1$
            return (tagName == null) ? "\"" + s + "\"" : //$NON-NLS-1$ //$NON-NLS-2$
                (s.length() == 0) ? "<" + tagName + "/>" : //$NON-NLS-1$ //$NON-NLS-2$
                "<" + tagName + ">" + s + "</" + tagName + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
    }
}
