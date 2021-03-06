/*
* Copyright (C) 2010 Mamadou Diop.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango.org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*
*/
package oma.xml.xdm.xcap_directory;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.doubango.imsdroid.utils.RFC3339Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

@Root(name = "xcap-directory", strict = false)
@Namespace(reference = "urn:oma:xml:xdm:xcap-directory")
public class XcapDirectory {

	@ElementList(entry = "folder", inline=true, required=false)
    protected List<XcapDirectory.Folder> folder;
    
    public List<XcapDirectory.Folder> getFolder() {
        if (folder == null) {
            folder = new ArrayList<XcapDirectory.Folder>();
        }
        return this.folder;
    }
    
    @Root(name = "folder", strict = false)
    public static class Folder {

    	@ElementList(entry = "entry", inline=true, required=false)
        protected List<XcapDirectory.Folder.Entry> entry;
        @Element(name = "error-code", required = false)
        protected String errorCode;
        @Attribute(required = true)
        protected String auid;
        
        public List<XcapDirectory.Folder.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<XcapDirectory.Folder.Entry>();
            }
            return this.entry;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
       
        public void setErrorCode(String value) {
            this.errorCode = value;
        }
        
        public String getAuid() {
            return auid;
        }
        
        public void setAuid(String value) {
            this.auid = value;
        }
        
        @Root(name = "entry", strict = false)
        public static class Entry {

            @Attribute(required = true)
            protected String uri;
            @Attribute(required = true)
            protected String etag;
            @Attribute(name = "last-modified", required = false)
            protected String _lastModified;
            @Attribute(required = false)
            protected BigInteger size;
            
            private Date lastModified;
            
            public String getUri() {
                return uri;
            }
            
            public void setUri(String value) {
                this.uri = value;
            }
            
            public String getEtag() {
                return etag;
            }
           
            public void setEtag(String value) {
                this.etag = value;
            }
          
            @Commit
            public void commit() {
            	if(this._lastModified != null){
            		try {
            			this.lastModified = RFC3339Date.parseRFC3339Date(this._lastModified);
        			} catch (IndexOutOfBoundsException e) {
        				e.printStackTrace();
        			} catch (ParseException e) {
        				e.printStackTrace();
        			}
        			this._lastModified = null;
            	}
            }
            
            public Date getLastModified() {
                return lastModified;
            }

            public void setLastModified(Date value) {
                this.lastModified = value;
            }
            
            public BigInteger getSize() {
                return size;
            }
            
            public void setSize(BigInteger value) {
                this.size = value;
            }
        }

    }

}
