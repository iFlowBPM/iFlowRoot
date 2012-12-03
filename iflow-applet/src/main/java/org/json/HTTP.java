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
 * Convert an HTTP header to a JSONObject and back.
 * @author JSON.org
 * @version 2008-09-18
 */
public class HTTP {

    /** Carriage return/line feed. */
    public static final String CRLF = "\r\n"; //$NON-NLS-1$

    /**
     * Convert an HTTP header string into a JSONObject. It can be a request
     * header or a response header. A request header will contain
     * <pre>{
     *    Method: "POST" (for example),
     *    "Request-URI": "/" (for example),
     *    "HTTP-Version": "HTTP/1.1" (for example)
     * }</pre>
     * A response header will contain
     * <pre>{
     *    "HTTP-Version": "HTTP/1.1" (for example),
     *    "Status-Code": "200" (for example),
     *    "Reason-Phrase": "OK" (for example)
     * }</pre>
     * In addition, the other parameters in the header will be captured, using
     * the HTTP field names as JSON names, so that <pre>
     *    Date: Sun, 26 May 2002 18:06:04 GMT
     *    Cookie: Q=q2=PPEAsg--; B=677gi6ouf29bn&b=2&f=s
     *    Cache-Control: no-cache</pre>
     * become
     * <pre>{...
     *    Date: "Sun, 26 May 2002 18:06:04 GMT",
     *    Cookie: "Q=q2=PPEAsg--; B=677gi6ouf29bn&b=2&f=s",
     *    "Cache-Control": "no-cache",
     * ...}</pre>
     * It does no further checking or conversion. It does not parse dates.
     * It does not do '%' transforms on URLs.
     * @param string An HTTP header string.
     * @return A JSONObject containing the elements and attributes
     * of the XML string.
     * @throws JSONException
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject     o = new JSONObject();
        HTTPTokener    x = new HTTPTokener(string);
        String         t;

        t = x.nextToken();
        if (t.toUpperCase().startsWith("HTTP")) { //$NON-NLS-1$

// Response

            o.put("HTTP-Version", t); //$NON-NLS-1$
            o.put("Status-Code", x.nextToken()); //$NON-NLS-1$
            o.put("Reason-Phrase", x.nextTo('\0')); //$NON-NLS-1$
            x.next();

        } else {

// Request

            o.put("Method", t); //$NON-NLS-1$
            o.put("Request-URI", x.nextToken()); //$NON-NLS-1$
            o.put("HTTP-Version", x.nextToken()); //$NON-NLS-1$
        }

// Fields

        while (x.more()) {
            String name = x.nextTo(':');
            x.next(':');
            o.put(name, x.nextTo('\0'));
            x.next();
        }
        return o;
    }


    /**
     * Convert a JSONObject into an HTTP header. A request header must contain
     * <pre>{
     *    Method: "POST" (for example),
     *    "Request-URI": "/" (for example),
     *    "HTTP-Version": "HTTP/1.1" (for example)
     * }</pre>
     * A response header must contain
     * <pre>{
     *    "HTTP-Version": "HTTP/1.1" (for example),
     *    "Status-Code": "200" (for example),
     *    "Reason-Phrase": "OK" (for example)
     * }</pre>
     * Any other members of the JSONObject will be output as HTTP fields.
     * The result will end with two CRLF pairs.
     * @param o A JSONObject
     * @return An HTTP header string.
     * @throws JSONException if the object does not contain enough
     *  information.
     */
    public static String toString(JSONObject o) throws JSONException {
        Iterator     keys = o.keys();
        String       s;
        StringBuffer sb = new StringBuffer();
        if (o.has("Status-Code") && o.has("Reason-Phrase")) { //$NON-NLS-1$ //$NON-NLS-2$
            sb.append(o.getString("HTTP-Version")); //$NON-NLS-1$
            sb.append(' ');
            sb.append(o.getString("Status-Code")); //$NON-NLS-1$
            sb.append(' ');
            sb.append(o.getString("Reason-Phrase")); //$NON-NLS-1$
        } else if (o.has("Method") && o.has("Request-URI")) { //$NON-NLS-1$ //$NON-NLS-2$
            sb.append(o.getString("Method")); //$NON-NLS-1$
            sb.append(' ');
            sb.append('"');
            sb.append(o.getString("Request-URI")); //$NON-NLS-1$
            sb.append('"');
            sb.append(' ');
            sb.append(o.getString("HTTP-Version")); //$NON-NLS-1$
        } else {
            throw new JSONException("Not enough material for an HTTP header."); //$NON-NLS-1$
        }
        sb.append(CRLF);
        while (keys.hasNext()) {
            s = keys.next().toString();
            if (!s.equals("HTTP-Version")      && !s.equals("Status-Code") && //$NON-NLS-1$ //$NON-NLS-2$
                    !s.equals("Reason-Phrase") && !s.equals("Method") && //$NON-NLS-1$ //$NON-NLS-2$
                    !s.equals("Request-URI")   && !o.isNull(s)) { //$NON-NLS-1$
                sb.append(s);
                sb.append(": "); //$NON-NLS-1$
                sb.append(o.getString(s));
                sb.append(CRLF);
            }
        }
        sb.append(CRLF);
        return sb.toString();
    }
}
