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

import pt.iflow.applet.Messages;

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

/**
 * Convert a web browser cookie specification to a JSONObject and back.
 * JSON and Cookies are both notations for name/value pairs.
 * @author JSON.org
 * @version 2008-09-18
 */
public class Cookie {

    /**
     * Produce a copy of a string in which the characters '+', '%', '=', ';'
     * and control characters are replaced with "%hh". This is a gentle form
     * of URL encoding, attempting to cause as little distortion to the
     * string as possible. The characters '=' and ';' are meta characters in
     * cookies. By convention, they are escaped using the URL-encoding. This is
     * only a convention, not a standard. Often, cookies are expected to have
     * encoded values. We encode '=' and ';' because we must. We encode '%' and
     * '+' because they are meta characters in URL encoding.
     * @param string The source string.
     * @return       The escaped result.
     */
    public static String escape(String string) {
        char         c;
        String       s = string.trim();
        StringBuffer sb = new StringBuffer();
        int          len = s.length();
        for (int i = 0; i < len; i += 1) {
            c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                sb.append(Character.forDigit((char)((c >>> 4) & 0x0f), 16));
                sb.append(Character.forDigit((char)(c & 0x0f), 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * Convert a cookie specification string into a JSONObject. The string
     * will contain a name value pair separated by '='. The name and the value
     * will be unescaped, possibly converting '+' and '%' sequences. The
     * cookie properties may follow, separated by ';', also represented as
     * name=value (except the secure property, which does not have a value).
     * The name will be stored under the key "name", and the value will be
     * stored under the key "value". This method does not do checking or
     * validation of the parameters. It only converts the cookie string into
     * a JSONObject.
     * @param string The cookie specification string.
     * @return A JSONObject containing "name", "value", and possibly other
     *  members.
     * @throws JSONException
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        String         n;
        JSONObject     o = new JSONObject();
        Object         v;
        JSONTokener x = new JSONTokener(string);
        o.put("name", x.nextTo('=')); //$NON-NLS-1$
        x.next('=');
        o.put("value", x.nextTo(';')); //$NON-NLS-1$
        x.next();
        while (x.more()) {
            n = unescape(x.nextTo("=;")); //$NON-NLS-1$
            if (x.next() != '=') {
                if (n.equals("secure")) { //$NON-NLS-1$
                    v = Boolean.TRUE;
                } else {
                    throw x.syntaxError("Missing '=' in cookie parameter."); //$NON-NLS-1$
                }
            } else {
                v = unescape(x.nextTo(';'));
                x.next();
            }
            o.put(n, v);
        }
        return o;
    }


    /**
     * Convert a JSONObject into a cookie specification string. The JSONObject
     * must contain "name" and "value" members.
     * If the JSONObject contains "expires", "domain", "path", or "secure"
     * members, they will be appended to the cookie specification string.
     * All other members are ignored.
     * @param o A JSONObject
     * @return A cookie specification string
     * @throws JSONException
     */
    public static String toString(JSONObject o) throws JSONException {
        StringBuffer sb = new StringBuffer();

        sb.append(escape(o.getString("name"))); //$NON-NLS-1$
        sb.append("="); //$NON-NLS-1$
        sb.append(escape(o.getString("value"))); //$NON-NLS-1$
        if (o.has("expires")) { //$NON-NLS-1$
            sb.append(";expires="); //$NON-NLS-1$
            sb.append(o.getString("expires")); //$NON-NLS-1$
        }
        if (o.has("domain")) { //$NON-NLS-1$
            sb.append(";domain="); //$NON-NLS-1$
            sb.append(escape(o.getString("domain"))); //$NON-NLS-1$
        }
        if (o.has("path")) { //$NON-NLS-1$
            sb.append(";path="); //$NON-NLS-1$
            sb.append(escape(o.getString("path"))); //$NON-NLS-1$
        }
        if (o.optBoolean("secure")) { //$NON-NLS-1$
            sb.append(";secure"); //$NON-NLS-1$
        }
        return sb.toString();
    }

    /**
     * Convert <code>%</code><i>hh</i> sequences to single characters, and
     * convert plus to space.
     * @param s A string that may contain
     *      <code>+</code>&nbsp;<small>(plus)</small> and
     *      <code>%</code><i>hh</i> sequences.
     * @return The unescaped string.
     */
    public static String unescape(String s) {
        int len = s.length();
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < len) {
                int d = JSONTokener.dehexchar(s.charAt(i + 1));
                int e = JSONTokener.dehexchar(s.charAt(i + 2));
                if (d >= 0 && e >= 0) {
                    c = (char)(d * 16 + e);
                    i += 2;
                }
            }
            b.append(c);
        }
        return b.toString();
    }
}
