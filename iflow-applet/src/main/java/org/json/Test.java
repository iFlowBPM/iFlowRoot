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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.io.StringWriter;

import pt.iflow.applet.Messages;

/**
 * Test class. This file is not formally a member of the org.json library.
 * It is just a casual test tool.
 */
public class Test {
	
    /**
     * Entry point.
     * @param args
     */
    public static void main(String args[]) {
        Iterator it;
        JSONArray a;
        JSONObject j;
        JSONStringer jj;
        String s;
        
/** 
 *  Obj is a typical class that implements JSONString. It also
 *  provides some beanie methods that can be used to 
 *  construct a JSONObject. It also demonstrates constructing
 *  a JSONObject with an array of names.
 */
        class Obj implements JSONString {
        	public String aString;
        	public double aNumber;
        	public boolean aBoolean;
        	
            public Obj(String string, double n, boolean b) {
                this.aString = string;
                this.aNumber = n;
                this.aBoolean = b;
            }
            
            public double getNumber() {
            	return this.aNumber;
            }
            
            public String getString() {
            	return this.aString;
            }
            
            public boolean isBoolean() {
            	return this.aBoolean;
            }
            
            public String getBENT() {
            	return "All uppercase key"; //$NON-NLS-1$
            }
            
            public String getX() {
            	return "x"; //$NON-NLS-1$
            }
            
            public String toJSONString() {
            	return "{" + JSONObject.quote(this.aString) + ":" +  //$NON-NLS-1$ //$NON-NLS-2$
            	JSONObject.doubleToString(this.aNumber) + "}"; //$NON-NLS-1$
            }            
            public String toString() {
            	return this.getString() + " " + this.getNumber() + " " +  //$NON-NLS-1$ //$NON-NLS-2$
            			this.isBoolean() + "." + this.getBENT() + " " + this.getX(); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }      
        

    	Obj obj = new Obj("A beany object", 42, true); //$NON-NLS-1$
        
        try {     
            j = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  "); //$NON-NLS-1$
            System.out.println(j.toString());

            s = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }"; //$NON-NLS-1$
            j = new JSONObject(s);
            System.out.println(j.toString(4));
            System.out.println(XML.toString(j));
                    
            s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> "; //$NON-NLS-1$
            j = XML.toJSONObject(s);
            System.out.println(j.toString(4));
            System.out.println();
            
            j = JSONML.toJSONObject(s);
            System.out.println(j.toString());
            System.out.println(JSONML.toString(j));
            System.out.println();
            
            a = JSONML.toJSONArray(s);
            System.out.println(a.toString(4));
            System.out.println(JSONML.toString(a));
            System.out.println();
            
            s = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>"; //$NON-NLS-1$
            j = JSONML.toJSONObject(s);
            System.out.println(j.toString(4));
            System.out.println(JSONML.toString(j));
            System.out.println();
            
            a = JSONML.toJSONArray(s);
            System.out.println(a.toString(4));
            System.out.println(JSONML.toString(a));
            System.out.println();
            
            s = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);
            System.out.println(j.toString(4));
            
            j = new JSONObject(obj);
            System.out.println(j.toString());
            
            s = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }"; //$NON-NLS-1$
            j = new JSONObject(s);
            System.out.println(j.toString(2));

            jj = new JSONStringer();
            s = jj
	            .object()
	                .key("single") //$NON-NLS-1$
	                .value("MARIE HAA'S") //$NON-NLS-1$
	                .key("Johnny") //$NON-NLS-1$
	                .value("MARIE HAA\\'S") //$NON-NLS-1$
	                .key("foo") //$NON-NLS-1$
	                .value("bar") //$NON-NLS-1$
	                .key("baz") //$NON-NLS-1$
	                .array()
	                    .object()
	                        .key("quux") //$NON-NLS-1$
	                        .value("Thanks, Josh!") //$NON-NLS-1$
	                    .endObject()
	                .endArray()
	                .key("obj keys") //$NON-NLS-1$
	                .value(JSONObject.getNames(obj))
	            .endObject()
            .toString();
            System.out.println(s);

            System.out.println(new JSONStringer()
                .object()
                	.key("a") //$NON-NLS-1$
                	.array()
                		.array()
                			.array()
                				.value("b") //$NON-NLS-1$
                            .endArray()
                        .endArray()
                    .endArray()
                .endObject()
                .toString());

            jj = new JSONStringer();
            jj.array();
            jj.value(1);
            jj.array();
            jj.value(null);
            jj.array();
            jj.object();
            jj.key("empty-array").array().endArray(); //$NON-NLS-1$
            jj.key("answer").value(42); //$NON-NLS-1$
            jj.key("null").value(null); //$NON-NLS-1$
            jj.key("false").value(false); //$NON-NLS-1$
            jj.key("true").value(true); //$NON-NLS-1$
            jj.key("big").value(123456789e+88); //$NON-NLS-1$
            jj.key("small").value(123456789e-88); //$NON-NLS-1$
            jj.key("empty-object").object().endObject(); //$NON-NLS-1$
            jj.key("long"); //$NON-NLS-1$
            jj.value(9223372036854775807L);
            jj.endObject();
            jj.value("two"); //$NON-NLS-1$
            jj.endArray();
            jj.value(true);
            jj.endArray();
            jj.value(98.6);
            jj.value(-100.0);
            jj.object();
            jj.endObject();
            jj.object();
            jj.key("one"); //$NON-NLS-1$
            jj.value(1.00);
            jj.endObject();
            jj.value(obj);
            jj.endArray();
            System.out.println(jj.toString());

            System.out.println(new JSONArray(jj.toString()).toString(4));

        	int ar[] = {1, 2, 3};
        	JSONArray ja = new JSONArray(ar);
        	System.out.println(ja.toString());
        	
        	String sa[] = {"aString", "aNumber", "aBoolean"};             //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            j = new JSONObject(obj, sa);
            j.put("Testing JSONString interface", obj); //$NON-NLS-1$
            System.out.println(j.toString(4));          
            
            j = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = new JSONObject(
                "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001," + //$NON-NLS-1$
                " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], " + //$NON-NLS-1$
                "  to   : null, op : 'Good'," + //$NON-NLS-1$
                "ten:10} postfix comment"); //$NON-NLS-1$
            j.put("String", "98.6"); //$NON-NLS-1$ //$NON-NLS-2$
            j.put("JSONObject", new JSONObject()); //$NON-NLS-1$
            j.put("JSONArray", new JSONArray()); //$NON-NLS-1$
            j.put("int", 57); //$NON-NLS-1$
            j.put("double", 123456789012345678901234567890.); //$NON-NLS-1$
            j.put("true", true); //$NON-NLS-1$
            j.put("false", false); //$NON-NLS-1$
            j.put("null", JSONObject.NULL); //$NON-NLS-1$
            j.put("bool", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            j.put("zero", -0.0); //$NON-NLS-1$
            j.put("\\u2028", "\u2028"); //$NON-NLS-1$ //$NON-NLS-2$
            j.put("\\u2029", "\u2029"); //$NON-NLS-1$ //$NON-NLS-2$
            a = j.getJSONArray("foo"); //$NON-NLS-1$
            a.put(666);
            a.put(2001.99);
            a.put("so \"fine\"."); //$NON-NLS-1$
            a.put("so <fine>."); //$NON-NLS-1$
            a.put(true);
            a.put(false);
            a.put(new JSONArray());
            a.put(new JSONObject());
            j.put("keys", JSONObject.getNames(j)); //$NON-NLS-1$
            System.out.println(j.toString(4));
            System.out.println(XML.toString(j));

            System.out.println("String: " + j.getDouble("String")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("  bool: " + j.getBoolean("bool")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("    to: " + j.getString("to")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("  true: " + j.getString("true")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("   foo: " + j.getJSONArray("foo")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("    op: " + j.getString("op")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("   ten: " + j.getInt("ten")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("  oops: " + j.optBoolean("oops")); //$NON-NLS-1$ //$NON-NLS-2$

            s = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$
            
            ja = JSONML.toJSONArray(s);
            System.out.println(ja.toString(4));
            System.out.println(JSONML.toString(ja));
            System.out.println(""); //$NON-NLS-1$
            
            s = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>"; //$NON-NLS-1$
            ja = JSONML.toJSONArray(s);
            System.out.println(ja.toString(4));
            System.out.println(JSONML.toString(ja));
            System.out.println(""); //$NON-NLS-1$

            s = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);

            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$
            ja = JSONML.toJSONArray(s);
            System.out.println(ja.toString(4));
            System.out.println(JSONML.toString(ja));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>"); //$NON-NLS-1$
            System.out.println(j.toString());
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(HTTP.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(HTTP.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println("isNull: " + j.isNull("nix")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("   has: " + j.has("nix")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(XML.toString(j));
            System.out.println(HTTP.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>"+"\n\n"+"<SOAP-ENV:Envelope"+ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
              " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""+ //$NON-NLS-1$
              " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\""+ //$NON-NLS-1$
              " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">"+ //$NON-NLS-1$
              "<SOAP-ENV:Body><ns1:doGoogleSearch"+ //$NON-NLS-1$
              " xmlns:ns1=\"urn:GoogleSearch\""+ //$NON-NLS-1$
              " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"+ //$NON-NLS-1$
              "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q"+ //$NON-NLS-1$
              " xsi:type=\"xsd:string\">'+search+'</q> <start"+ //$NON-NLS-1$
              " xsi:type=\"xsd:int\">0</start> <maxResults"+ //$NON-NLS-1$
              " xsi:type=\"xsd:int\">10</maxResults> <filter"+ //$NON-NLS-1$
              " xsi:type=\"xsd:boolean\">true</filter> <restrict"+ //$NON-NLS-1$
              " xsi:type=\"xsd:string\"></restrict> <safeSearch"+ //$NON-NLS-1$
              " xsi:type=\"xsd:boolean\">false</safeSearch> <lr"+ //$NON-NLS-1$
              " xsi:type=\"xsd:string\"></lr> <ie"+ //$NON-NLS-1$
              " xsi:type=\"xsd:string\">latin1</ie> <oe"+ //$NON-NLS-1$
              " xsi:type=\"xsd:string\">latin1</oe>"+ //$NON-NLS-1$
              "</ns1:doGoogleSearch>"+ //$NON-NLS-1$
              "</SOAP-ENV:Body></SOAP-ENV:Envelope>"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo "); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(CookieList.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002"); //$NON-NLS-1$
            System.out.println(j.toString(2));
            System.out.println(Cookie.toString(j));
            System.out.println(""); //$NON-NLS-1$

            j = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}"); //$NON-NLS-1$
            System.out.println(j.toString());
            System.out.println(""); //$NON-NLS-1$

            JSONTokener jt = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}"); //$NON-NLS-1$
            j = new JSONObject(jt);
            System.out.println(j.toString());
            System.out.println("pre: " + j.optInt("pre")); //$NON-NLS-1$ //$NON-NLS-2$
            int i = jt.skipTo('{');
            System.out.println(i);
            j = new JSONObject(jt);
            System.out.println(j.toString());
            System.out.println(""); //$NON-NLS-1$

            a = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n"); //$NON-NLS-1$

            s = CDL.toString(a);
            System.out.println(s);
            System.out.println(""); //$NON-NLS-1$
            System.out.println(a.toString(4));
            System.out.println(""); //$NON-NLS-1$
            a = CDL.toJSONArray(s);
            System.out.println(a.toString(4));
            System.out.println(""); //$NON-NLS-1$

            a = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] "); //$NON-NLS-1$
            System.out.println(a.toString());
            System.out.println(""); //$NON-NLS-1$
            System.out.println(XML.toString(a));
            System.out.println(""); //$NON-NLS-1$

            j = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}"); //$NON-NLS-1$
            System.out.println(j.toString(4));
            System.out.println(""); //$NON-NLS-1$
            if (j.getBoolean("true") && !j.getBoolean("false")) { //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("It's all good"); //$NON-NLS-1$
            }

            System.out.println(""); //$NON-NLS-1$
            j = new JSONObject(j, new String[]{"dec", "oct", "hex", "missing"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            System.out.println(j.toString(4));

            System.out.println(""); //$NON-NLS-1$
            System.out.println(new JSONStringer().array().value(a).value(j).endArray());

            j = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}"); //$NON-NLS-1$
            System.out.println(j.toString(4));

            System.out.println("\ngetInt"); //$NON-NLS-1$
            System.out.println("int    " + j.getInt("int")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("long   " + j.getInt("long")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("longer " + j.getInt("longer")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("double " + j.getInt("double")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("string " + j.getInt("string")); //$NON-NLS-1$ //$NON-NLS-2$

            System.out.println("\ngetLong"); //$NON-NLS-1$
            System.out.println("int    " + j.getLong("int")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("long   " + j.getLong("long")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("longer " + j.getLong("longer")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("double " + j.getLong("double")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("string " + j.getLong("string")); //$NON-NLS-1$ //$NON-NLS-2$

            System.out.println("\ngetDouble"); //$NON-NLS-1$
            System.out.println("int    " + j.getDouble("int")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("long   " + j.getDouble("long")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("longer " + j.getDouble("longer")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("double " + j.getDouble("double")); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("string " + j.getDouble("string")); //$NON-NLS-1$ //$NON-NLS-2$

            j.put("good sized", 9223372036854775807L); //$NON-NLS-1$
            System.out.println(j.toString(4));

            a = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]"); //$NON-NLS-1$
            System.out.println(a.toString(4));

            System.out.println("\nKeys: "); //$NON-NLS-1$
            it = j.keys();
            while (it.hasNext()) {
                s = (String)it.next();
                System.out.println(s + ": " + j.getString(s)); //$NON-NLS-1$
            }


            System.out.println("\naccumulate: "); //$NON-NLS-1$
            j = new JSONObject();
            j.accumulate("stooge", "Curly"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stooge", "Larry"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stooge", "Moe"); //$NON-NLS-1$ //$NON-NLS-2$
            a = j.getJSONArray("stooge"); //$NON-NLS-1$
            a.put(5, "Shemp"); //$NON-NLS-1$
            System.out.println(j.toString(4));

            System.out.println("\nwrite:"); //$NON-NLS-1$
            System.out.println(j.write(new StringWriter()));

            s = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);
            System.out.println(j.toString(4));
            System.out.println(XML.toString(j));
            
            s = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);
            System.out.println(j.toString(4));
            System.out.println(XML.toString(j));
            
            a = JSONML.toJSONArray(s);
            System.out.println(a.toString(4));
            System.out.println(JSONML.toString(a));
            
            Collection c = null;
            Map m = null;
            
            j = new JSONObject(m);
            a = new JSONArray(c);
            j.append("stooge", "Joe DeRita"); //$NON-NLS-1$ //$NON-NLS-2$
            j.append("stooge", "Shemp"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stooges", "Curly"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stooges", "Larry"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stooges", "Moe"); //$NON-NLS-1$ //$NON-NLS-2$
            j.accumulate("stoogearray", j.get("stooges")); //$NON-NLS-1$ //$NON-NLS-2$
            j.put("map", m); //$NON-NLS-1$
            j.put("collection", c); //$NON-NLS-1$
            j.put("array", a); //$NON-NLS-1$
            a.put(m);
            a.put(c);
            System.out.println(j.toString(4));
            
            s = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";  //$NON-NLS-1$
            j = new JSONObject(s);
            System.out.println(j.toString(4));
            
            s = " (\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\")"; //$NON-NLS-1$
            a = new JSONArray(s);
            System.out.println(a.toString());
            
            s = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>"; //$NON-NLS-1$
            j = XML.toJSONObject(s);

            System.out.println(j.toString(2));
            System.out.println(XML.toString(j));
            System.out.println(""); //$NON-NLS-1$
            ja = JSONML.toJSONArray(s);
            System.out.println(ja.toString(4));
            System.out.println(JSONML.toString(ja));
            System.out.println(""); //$NON-NLS-1$
          
            
            System.out.println("\nTesting Exceptions: "); //$NON-NLS-1$

            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                a = new JSONArray();
                a.put(Double.NEGATIVE_INFINITY);
                a.put(Double.NaN);
                System.out.println(a.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(j.getDouble("stooge")); //$NON-NLS-1$
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(j.getDouble("howard")); //$NON-NLS-1$
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(j.put(null, "howard")); //$NON-NLS-1$
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(a.getDouble(0));
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(a.get(-1));
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
                System.out.println(a.put(Double.NaN));
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
            	j = XML.toJSONObject("<a><b>    "); //$NON-NLS-1$
            } catch (Exception e) {
            	System.out.println(e);
            }            
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
            	j = XML.toJSONObject("<a></b>    "); //$NON-NLS-1$
            } catch (Exception e) {
            	System.out.println(e);
            }            
            System.out.print("Exception: "); //$NON-NLS-1$
            try {
            	j = XML.toJSONObject("<a></a    "); //$NON-NLS-1$
            } catch (Exception e) {
            	System.out.println(e);
            }
            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
            	ja = new JSONArray(new Object());
            	System.out.println(ja.toString());
            } catch (Exception e) {
            	System.out.println(e);
            }

            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
            	s = "[)"; //$NON-NLS-1$
            	a = new JSONArray(s);
            	System.out.println(a.toString());
            } catch (Exception e) {
            	System.out.println(e);
            }

            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
                s = "<xml"; //$NON-NLS-1$
                ja = JSONML.toJSONArray(s);
                System.out.println(ja.toString(4));
            } catch (Exception e) {
            	System.out.println(e);
            }

            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
                s = "<right></wrong>"; //$NON-NLS-1$
                ja = JSONML.toJSONArray(s);
                System.out.println(ja.toString(4));
            } catch (Exception e) {
            	System.out.println(e);
            }

            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
                s = "{\"koda\": true, \"koda\": true}"; //$NON-NLS-1$
                j = new JSONObject(s);
                System.out.println(j.toString(4));
            } catch (Exception e) {
            	System.out.println(e);
            }

            System.out.print("Exception: "); //$NON-NLS-1$
            try {            	
                jj = new JSONStringer();
                s = jj
    	            .object()
    	                .key("bosanda") //$NON-NLS-1$
    	                .value("MARIE HAA'S") //$NON-NLS-1$
    	                .key("bosanda") //$NON-NLS-1$
    	                .value("MARIE HAA\\'S") //$NON-NLS-1$
    	            .endObject()
    	            .toString();
                System.out.println(j.toString(4));
            } catch (Exception e) {
            	System.out.println(e);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
