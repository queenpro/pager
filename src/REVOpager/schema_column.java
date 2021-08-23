/*
 * SOFTWARE BY FFS RELEASED UNDER AGPL LICENSE.
 * REFER TO WWW.FFS.IT AND INFO@FFS.IT FOR INFO.
 * Author: Franco Venezia
  
    Copyright (C) <2019>  <Franco Venezia @ ffs.it>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package REVOpager;

import java.math.BigInteger;
 
public class schema_column {
  
     
   String COLUMN_NAME;
    String COLUMN_DEFAULT;
    int ORDINAL_POSITION;
    String DATA_TYPE;
    BigInteger CHARACTER_MAXIMUM_LENGTH;
    String IS_NULLABLE; // ES. 'YES' oppure 'NO'
    String COLUMN_KEY; // ES. 'PRI'=PRIMARY
    String EXTRA; // ES. 'auto_increment'
String COLUMN_TYPE;
int NUMERIC_PRECISION;

    public String getCOLUMN_TYPE() {
        return COLUMN_TYPE;
    }

    public void setCOLUMN_TYPE(String COLUMN_TYPE) {
        this.COLUMN_TYPE = COLUMN_TYPE;
    }

    public int getNUMERIC_PRECISION() {
        return NUMERIC_PRECISION;
    }

    public void setNUMERIC_PRECISION(int NUMERIC_PRECISION) {
        this.NUMERIC_PRECISION = NUMERIC_PRECISION;
    }

 
    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public String getCOLUMN_DEFAULT() {
        return COLUMN_DEFAULT;
    }

    public void setCOLUMN_DEFAULT(String COLUMN_DEFAULT) {
        this.COLUMN_DEFAULT = COLUMN_DEFAULT;
    }
 
    public int getORDINAL_POSITION() {
        return ORDINAL_POSITION;
    }

    public void setORDINAL_POSITION(int ORDINAL_POSITION) {
        this.ORDINAL_POSITION = ORDINAL_POSITION;
    }

    public String  getDATA_TYPE() {
        return DATA_TYPE;
    }

    public void setDATA_TYPE(String  DATA_TYPE) {
        this.DATA_TYPE = DATA_TYPE;
    }

    public BigInteger getCHARACTER_MAXIMUM_LENGTH() {
        return CHARACTER_MAXIMUM_LENGTH;
    }

    public void setCHARACTER_MAXIMUM_LENGTH(BigInteger CHARACTER_MAXIMUM_LENGTH) {
        this.CHARACTER_MAXIMUM_LENGTH = CHARACTER_MAXIMUM_LENGTH;
    }

 

   

    public String getIS_NULLABLE() {
        return IS_NULLABLE;
    }

    public void setIS_NULLABLE(String IS_NULLABLE) {
        this.IS_NULLABLE = IS_NULLABLE;
    }

    public String getCOLUMN_KEY() {
        return COLUMN_KEY;
    }

    public void setCOLUMN_KEY(String COLUMN_KEY) {
        this.COLUMN_KEY = COLUMN_KEY;
    }

    public String getEXTRA() {
        return EXTRA;
    }

    public void setEXTRA(String EXTRA) {
        this.EXTRA = EXTRA;
    }

    
    
    
    
}
