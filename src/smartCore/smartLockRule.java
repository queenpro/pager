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


package smartCore;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class smartLockRule {
    
  

        String ruleType;
        String typeA;
        String fieldA;
        String fieldTypeA;
        String valueA;
        String typeB;
        String fieldB;
        String fieldTypeB;
        String valueB;
        String test = "";
        int level;
        int right;
        String limitUp;
        String limitDown;

        String tabCorrelazioni;
        String tabGruppi;
        String fieldIDinTabGruppi;

        public String getTabCorrelazioni() {
            return tabCorrelazioni;
        }

        public void setTabCorrelazioni(String tabCorrelazioni) {
            this.tabCorrelazioni = tabCorrelazioni;
        }

        public String getTabGruppi() {
            return tabGruppi;
        }

        public void setTabGruppi(String tabGruppi) {
            this.tabGruppi = tabGruppi;
        }

        public String getFieldIDinTabGruppi() {
            return fieldIDinTabGruppi;
        }

        public void setFieldIDinTabGruppi(String fieldIDinTabGruppi) {
            this.fieldIDinTabGruppi = fieldIDinTabGruppi;
        }

        public String getRuleType() {
            return ruleType;
        }

        public void setRuleType(String ruleType) {
            this.ruleType = ruleType;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getFieldTypeA() {
            return fieldTypeA;
        }

        public void setFieldTypeA(String fieldTypeA) {
            this.fieldTypeA = fieldTypeA;
        }

        public String getFieldTypeB() {
            return fieldTypeB;
        }

        public void setFieldTypeB(String fieldTypeB) {
            this.fieldTypeB = fieldTypeB;
        }

        public String getTypeA() {
            return typeA;
        }

        public void setTypeA(String typeA) {
            this.typeA = typeA;
        }

        public String getFieldA() {
            return fieldA;
        }

        public void setFieldA(String fieldA) {
            this.fieldA = fieldA;
        }

        public String getValueA() {
            return valueA;
        }

        public void setValueA(String valueA) {
            this.valueA = valueA;
        }

        public String getTypeB() {
            return typeB;
        }

        public void setTypeB(String typeB) {
            this.typeB = typeB;
        }

        public String getFieldB() {
            return fieldB;
        }

        public void setFieldB(String fieldB) {
            this.fieldB = fieldB;
        }

        public String getValueB() {
            return valueB;
        }

        public void setValueB(String valueB) {
            this.valueB = valueB;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public String getLimitUp() {
            return limitUp;
        }

        public void setLimitUp(String limitUp) {
            this.limitUp = limitUp;
        }

        public String getLimitDown() {
            return limitDown;
        }

        public void setLimitDown(String limitDown) {
            this.limitDown = limitDown;
        }

    

}
