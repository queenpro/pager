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


package models;

import java.sql.Blob;
import java.sql.ResultSet;
import models.SelectList;


/**
 *
 * @author Franco
 */
public class ShowItObject {

    public ResultSet rs;
    public container C;
    public contentGenerator CG;
    public content Content;
    public origin Origin;

    public String ID;
    public String name;
    public String labelHeader;
    public String rifProject;
    public String rifForm;
    public String position;
    public String defaultValue;

    public int AddingRow_enabled;
    public String AddingRow_params;

    public String primary;
    public String autoCompiled;
    public String ValueToWrite;
    public String triggeredStyle;

    public String actionPerformed;
    public String actionParams;

    public String visible;
    public int actuallyVisible;
    public int actualRights;

    public String routineOnChange;
    public Blob picture;
    public String ges_routineOnLoad;
    public String ges_triggers;

    public objRight objRights;

    public ShowItObject() {
     }

   

    public class container {

        public String Type;
        public boolean visible;
        public String JsClass;
        public String Width;
        public String Heigth;

        public String conditionalBackColor;
        public String contitionalFontType;
        public String conditionalFontSize;
        public String conditionalFontColor;

        public String conditionalImageURL;
        public String conditionalLabel;
        public String defaultBackColor;
        public String defaultFontType;
        public String defaultFontSize;
        public String defaultFontColor;

        public String defaultStyle;
        public String conditionalStyle;

        public String getDefaultStyle() {
            return defaultStyle;
        }

        public void setDefaultStyle(String defaultStyle) {
            this.defaultStyle = defaultStyle;
        }

        public String getConditionalStyle() {
            return conditionalStyle;
        }

        public void setConditionalStyle(String conditionalStyle) {
            this.conditionalStyle = conditionalStyle;
        }

        public String getConditionalFontColor() {
            return conditionalFontColor;
        }

        public void setConditionalFontColor(String conditionalFontColor) {
            this.conditionalFontColor = conditionalFontColor;
        }

        public String getDefaultBackColor() {
            return defaultBackColor;
        }

        public void setDefaultBackColor(String defaultBackColor) {
            this.defaultBackColor = defaultBackColor;
        }

        public String getDefaultFontType() {
            return defaultFontType;
        }

        public void setDefaultFontType(String defaultFontType) {
            this.defaultFontType = defaultFontType;
        }

        public String getDefaultFontSize() {
            return defaultFontSize;
        }

        public void setDefaultFontSize(String defaultFontSize) {
            this.defaultFontSize = defaultFontSize;
        }

        public String getDefaultFontColor() {
            return defaultFontColor;
        }

        public void setDefaultFontColor(String defaultFontColor) {
            this.defaultFontColor = defaultFontColor;
        }

        public String getConditionalBackColor() {
            return conditionalBackColor;
        }

        public void setConditionalBackColor(String conditionalBackColor) {
            this.conditionalBackColor = conditionalBackColor;
        }

        public String getContitionalFontType() {
            return contitionalFontType;
        }

        public void setContitionalFontType(String contitionalFontType) {
            this.contitionalFontType = contitionalFontType;
        }

        public String getConditionalFontSize() {
            return conditionalFontSize;
        }

        public void setConditionalFontSize(String conditionalFontSize) {
            this.conditionalFontSize = conditionalFontSize;
        }

        public String getConditionalImageURL() {
            return conditionalImageURL;
        }

        public void setConditionalImageURL(String conditionalImageURL) {
            this.conditionalImageURL = conditionalImageURL;
        }

        public String getConditionalLabel() {
            return conditionalLabel;
        }

        public void setConditionalLabel(String conditionalLabel) {
            this.conditionalLabel = conditionalLabel;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getJsClass() {
            return JsClass;
        }

        public void setJsClass(String JsClass) {
            this.JsClass = JsClass;
        }

        public String getWidth() {
            return Width;
        }

        public void setWidth(String Width) {
            this.Width = Width;
        }

        public String getHeigth() {
            return Heigth;
        }

        public void setHeigth(String Heigth) {
            this.Heigth = Heigth;
        }

    }

    public class origin {

        public SelectList selectList;
        public String query;
        public String labelField;
        public String valueField;
        public String valueFieldType;

        public SelectList getSelectList() {
            return selectList;
        }

        public void setSelectList(SelectList selectList) {
            this.selectList = selectList;
        }

        public String getValueFieldType() {
            return valueFieldType;
        }

        public void setValueFieldType(String valueFieldType) {
            this.valueFieldType = valueFieldType;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getLabelField() {
            return labelField;
        }

        public void setLabelField(String labelField) {
            this.labelField = labelField;
        }

        public String getValueField() {
            return valueField;
        }

        public void setValueField(String valueField) {
            this.valueField = valueField;
        }

    }

    public class contentGenerator {

        public String Type;
        public String Params;
        public String Value;

        /*String OriginTable; // se il field è fuori dalla mainTable del firm, indico qui la Table per ricavare le caratteristiche
         String OriginFilter;

         public String getOriginTable() {
         return OriginTable;
         }

         public void setOriginTable(String OriginTable) {
         this.OriginTable = OriginTable;
         }

         public String getOriginFilter() {
         return OriginFilter;
         }

         public void setOriginFilter(String OriginFilter) {
         this.OriginFilter = OriginFilter;
         }
        
         */
        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getParams() {
            return Params;
        }

        public void setParams(String Params) {
            this.Params = Params;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }

    }

    public class content {

        public String Type;
        public String Value;
        public int hasSum;
        public String modifiable;
        public int actualSum;
        public int actualModifiable;
        public int thisRowModifiable;

        public boolean primaryFieldAutocompiled;

        public boolean isPrimaryFieldAutocompiled() {
            return primaryFieldAutocompiled;
        }

        public void setPrimaryFieldAutocompiled(boolean primaryFieldAutocompiled) {
            this.primaryFieldAutocompiled = primaryFieldAutocompiled;
        }

        public int getThisRowModifiable() {
            return thisRowModifiable;
        }

        public void setThisRowModifiable(int thisRowModifiable) {
            this.thisRowModifiable = thisRowModifiable;
        }

        public int getActualSum() {
            return actualSum;
        }

        public void setActualSum(int actualSum) {
            this.actualSum = actualSum;
        }

        public int getActualModifiable() {
            return actualModifiable;
        }

        public void setActualModifiable(int actualModifiable) {
            this.actualModifiable = actualModifiable;
        }

        public int getHasSum() {
            return hasSum;
        }

        public void setHasSum(int hasSum) {
            this.hasSum = hasSum;
        }

        public String getModifiable() {
            return modifiable;
        }

        public void setModifiable(String modifiable) {
            this.modifiable = modifiable;
        }

        public String getType() {
            return Type;
        }

        public void setType(String Type) {
            this.Type = Type;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }

    }

    public int getActualRights() {
        return actualRights;
    }

    public void setActualRights(int actualRights) {
        this.actualRights = actualRights;
    }

    public String getGes_triggers() {
        return ges_triggers;
    }

    public void setGes_triggers(String ges_triggers) {
        this.ges_triggers = ges_triggers;
    }

    public String getGes_routineOnLoad() {
        return ges_routineOnLoad;
    }

    public void setGes_routineOnLoad(String ges_routineOnLoad) {
        this.ges_routineOnLoad = ges_routineOnLoad;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRoutineOnChange() {
        return routineOnChange;
    }

    public void setRoutineOnChange(String routineOnChange) {
        this.routineOnChange = routineOnChange;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public int getActuallyVisible() {
        return actuallyVisible;
    }

    public void setActuallyVisible(int actuallyVisible) {
        this.actuallyVisible = actuallyVisible;
    }

    public String getActionParams() {
        return actionParams;
    }

    public void setActionParams(String actionParams) {
        this.actionParams = actionParams;
    }

    public String getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(String actionPerformed) {
        this.actionPerformed = actionPerformed;
    }

    public String getValueToWrite() {
        return ValueToWrite;
    }

    public void setValueToWrite(String ValueToWrite) {
        this.ValueToWrite = ValueToWrite;
    }

    public String getLabelHeader() {
        return labelHeader;
    }

    public void setLabelHeader(String labelHeader) {
        this.labelHeader = labelHeader;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getAutoCompiled() {
        return autoCompiled;
    }

    public void setAutoCompiled(String autoCompiled) {
        this.autoCompiled = autoCompiled;
    }

    public int getAddingRow_enabled() {
        return AddingRow_enabled;
    }

    public void setAddingRow_enabled(int AddingRow_enabled) {
        this.AddingRow_enabled = AddingRow_enabled;
    }

    public String getAddingRow_params() {
        return AddingRow_params;
    }

    public void setAddingRow_params(String AddingRow_params) {
        this.AddingRow_params = AddingRow_params;
    }

    public String getTriggeredStyle() {
        return triggeredStyle;
    }

    public void setTriggeredStyle(String triggeredStyle) {
        this.triggeredStyle = triggeredStyle;
    }

    public ShowItObject(String name) {
        this.name = name;
        this.C = new container();
        this.CG = new contentGenerator();
        this.Content = new content();
        this.Origin = new origin();
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

    /*
     public String paintObject() {
     String htmlCode = "";


     //1. ricavo il valore da scrivere (content) usando il contentGenerator
        
     if (CG.Type.equalsIgnoreCase("FIELD")){
            
     }
        
        
        
     //2. costruisco il container
        
     //3. riempio il container con il content
        
        
        
        
        
        
        
        
     
     return htmlCode;
       
     }
    
    
    
     */
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRifProject() {
        return rifProject;
    }

    public void setRifProject(String rifProject) {
        this.rifProject = rifProject;
    }

    public String getRifForm() {
        return rifForm;
    }

    public void setRifForm(String rifForm) {
        this.rifForm = rifForm;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void loadFromDB(ResultSet rs) {
        ShowItObject myObject = this;

        try {
            myObject.setID(rs.getString("ID"));
        } catch (Exception e) {
        }
        try {
            myObject.setName(rs.getString("name"));
        } catch (Exception e) {
        }
        try {
            myObject.setDefaultValue(rs.getString("defaultValue"));
        } catch (Exception e) {
        }

        try {
            myObject.setLabelHeader(rs.getString("labelHeader"));
        } catch (Exception e) {
        }
        try {
            myObject.setPosition(rs.getString("position"));
        } catch (Exception e) {
        }
        try {
            myObject.CG.setType(rs.getString("CGtype"));
        } catch (Exception e) {
        }
        try {
            myObject.CG.setValue(rs.getString("CGvalue"));
        } catch (Exception e) {
        }
        try {
            myObject.CG.setParams(rs.getString("CGparams"));
        } catch (Exception e) {
        }
        try {
            myObject.C.setType(rs.getString("containerType"));
        } catch (Exception e) {
        }
        try {
            myObject.C.setJsClass(rs.getString("containerClass"));
        } catch (Exception e) {
        }
        try {
            myObject.C.setWidth(rs.getString("containerWidth"));
        } catch (Exception e) {
        }
        try {
            myObject.C.setHeigth(rs.getString("containerHeight"));
        } catch (Exception e) {
        }
        try {
            myObject.Content.setType(rs.getString("contentType"));
        } catch (Exception e) {
        }
        try {
            myObject.setAddingRow_enabled(rs.getInt("AddingRow_enabled"));
        } catch (Exception e) {
        }
        try {
            myObject.setAddingRow_params(rs.getString("AddingRow_params"));
        } catch (Exception e) {
        }
        try {
            myObject.Origin.setQuery(rs.getString("originQuery"));
        } catch (Exception e) {
        }
        try {
            myObject.Origin.setLabelField(rs.getString("originLabelField"));
        } catch (Exception e) {
        }
        try {
            myObject.Origin.setValueField(rs.getString("originValueField"));
        } catch (Exception e) {
        }
        try {
            myObject.Origin.setValueFieldType(rs.getString("originValueFieldType"));
        } catch (Exception e) {
        }
        try {
            myObject.setActionPerformed(rs.getString("actionPerformed"));
        } catch (Exception e) {
        }
        try {
            myObject.setActionParams(rs.getString("actionParams"));
        } catch (Exception e) {
        }
        try {
            myObject.setVisible(rs.getString("visible"));
        } catch (Exception e) {
        }

        try {
            myObject.setRoutineOnChange(rs.getString("routineOnChange"));
        } catch (Exception e) {
        }

        try {
            myObject.Content.setModifiable(rs.getString("modifiable"));
        } catch (Exception e) {
        }
        try {
            myObject.Content.setHasSum(rs.getInt("hasSum"));
        } catch (Exception e) {
        }

        try {
            myObject.setGes_routineOnLoad(rs.getString("routineOnChange"));
        } catch (Exception e) {
        }
        try {
            myObject.setGes_triggers(rs.getString("ges_triggers"));
        } catch (Exception e) {
        }

    }
    public objRight createVoidRights(){
        objRight myRight = new objRight(-1);
        return myRight;
    }
    
    
     public objRight createNewRights(int rightValue){
        objRight myRight = new objRight(rightValue);
        return myRight;
    }
    public class objRight {

       public int level;
      public  int totalRight;
       public int canView;//1
      public  int canModify;//2
      public  int canDelete;//4
      public  int canCreate;//8
      public  int canPushButton;//16
      public  int canEverything;//128
      public  String Type; // se i diritti sono in una riga di add lo devo sapere per modificarli di conseguenza

        public void print() {
            System.out.println("level*********" + this.level);
            System.out.println("1.canView***************" + this.canView);
            System.out.println("2.canModify*************" + this.canModify);
            System.out.println("4.canDelete*************" + this.canDelete);
            System.out.println("8.canCreate*************" + this.canCreate);
            System.out.println("16.canPushButton*********" + this.canPushButton);
            System.out.println("128.canEverything*********" + this.canEverything);
        }

        public objRight(int totalRight) {
            this.totalRight = totalRight;
            //System.out.println("ANALIZZO RIGHTS:" + totalRight);
            evaluateRights();

        }

        public void evaluateRights() {
            int tr = totalRight;
            if (tr < 0) {
                canEverything = -1;
                canPushButton = -1;
                canCreate = -1;
                canDelete = -1;
                canModify = -1;
                canView = -1;

                return;
            }
            if ((tr - 128) >= 0) {
                canEverything = 1;
                tr = tr - 128;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canEverything = 0;
            }
            if ((tr - 16) >= 0) {
                canPushButton = 1;
                tr = tr - 16;
                //      System.out.println("  RIGHTS residui :" + tr);
            } else {
                canPushButton = 0;
            }
            if ((tr - 8) >= 0) {
                canCreate = 1;
                tr = tr - 8;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canCreate = 0;
            }
            if ((tr - 4) >= 0) {
                canDelete = 1;
                tr = tr - 4;
                //       System.out.println("  RIGHTS residui :" + tr);
            } else {
                canDelete = 0;
            }
            if ((tr - 2) >= 0) {
                canModify = 1;
                tr = tr - 2;
                //       System.out.println("  RIGHTS residui :" + tr);
            } else {
                canModify = 0;
            }
            if ((tr - 1) >= 0) {
                canView = 1;
                tr = tr - 1;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canView = 0;
            }
        }

    }
    
}
