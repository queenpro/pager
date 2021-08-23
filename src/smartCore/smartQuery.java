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

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class smartQuery {

    String query;
    String formVisualFilteredElements;
    String formVisualFilter;
    String newQuery;

    public smartQuery(String query, String formVisualFilteredElements, String formVisualFilter) {
        this.query = query;
        this.formVisualFilteredElements = formVisualFilteredElements;
        this.formVisualFilter = formVisualFilter;
    }

    public String regenerateQuery(String myWhereClause, String myOrderBy, String myGroupby, boolean keepOriginalWhere, String joinWhereOrAnd, boolean keepOriginalOrderby, boolean keepOriginalGroupby) {
    
        //myForm.queryUsed = mySquery.regenerateQuery(newFilter, newOrder, newGroup, true, "AND", true, true);
        String originalQuery = query;
        String condizioni = "";
        newQuery = originalQuery;
//        System.out.println("\nregenerateQuery->originalQuery = " + originalQuery);
//        System.out.println("myWhereClause = " + myWhereClause); 
//        System.out.println("myOrderBy = " + myOrderBy);
        if (originalQuery != null && originalQuery.length() > 1) {
            /*
             String text = "0123hello9012hello8901hello7890";
             String word = "hello";

             System.out.println(text.indexOf(word)); // prints "4"
             System.out.println(text.lastIndexOf(word)); // prints "22"

             // find all occurrences forward
             for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
             System.out.println(i);
             } // prints "4", "13", "22"

             // find all occurrences backward
             for (int i = text.length(); (i = text.lastIndexOf(word, i - 1)) != -1; i++) {
             System.out.println(i);
             } // prints "22", "13", "4"
             */
//            String smartTail = "";
            String originalGroupBy = "";
            String originalOrderBy = "";
            String staticWhere = "";
            String afterWHERE = "";
            String smartPartToKeep = "";
//            System.out.println("----SMARTregenerateQuery--- query iniziale:" + originalQuery);
            //1. cerco la posizione dell'ultimo WHERE           
            int lastWHEREposition = 0;
            String text = originalQuery;
            String word = "WHERE";
            for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                lastWHEREposition = i;
            }

            smartPartToKeep = originalQuery;
            if (lastWHEREposition <= 0) {
                // non ci sono WHERE
                afterWHERE = originalQuery;
                text = afterWHERE;
                int lastGROPUPBYposition = 0;
                word = "GROUP BY";
                for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                    lastGROPUPBYposition = i;
                }
                if (lastGROPUPBYposition <= 0) {
                    // non ci sono GROPUPBY.. CERCO almeno un ORDER BY
                    int lastORDERBYposition = 0;
                    word = "ORDER BY";
                    for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                        lastORDERBYposition = i;
                    }
                    if (lastORDERBYposition <= 0) {
                        // non ci sono orderby

                    } else {
                        //c'è un orderby da considerare dopo il where
                        smartPartToKeep = originalQuery.substring(0, lastORDERBYposition);
                        originalOrderBy = text.substring(lastORDERBYposition + 8, text.length());
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    smartPartToKeep = originalQuery.substring(0, lastGROPUPBYposition);
                    originalGroupBy = text.substring(lastGROPUPBYposition + 8, text.length());
                }

            } else {
//                System.out.println("----SMART--- posizione ultimo WHERE:" + lastWHEREposition);
                //2. tutta la parte di testo prima del WHERE è da tenere          
                smartPartToKeep = originalQuery.substring(0, lastWHEREposition);
//                System.out.println("----SMART--- da tenere:" + smartPartToKeep);

                afterWHERE = originalQuery.substring(lastWHEREposition + 5, this.query.length());
//                System.out.println("----SMART--- afterWHERE:" + afterWHERE);

                text = afterWHERE + " ";
                int lastGROPUPBYposition = 0;
                word = "GROUP BY";
                for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                    lastGROPUPBYposition = i;
                }
                if (lastGROPUPBYposition <= 0) {
                    // non ci sono GROPUPBY.. CERCO almeno un ORDER BY
                    int lastORDERBYposition = 0;
                    word = "ORDER BY";
                    for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                        lastORDERBYposition = i;
                    }
                    if (lastORDERBYposition <= 0) {
                        // non ci sono orderby
                        staticWhere = text;
//                        System.out.println("----SMART---non ci sono orderby staticWhere:" + staticWhere);
                    } else {
                        //c'è un orderby da considerare dopo il where
                        originalOrderBy = text.substring(lastORDERBYposition + 8, text.length());
                        staticWhere = text.substring(0, lastORDERBYposition);
                    }
                } else {
                    //c'è un groupby da considerare dopo il where e se c'è conterrà anche l'order by
                    originalGroupBy = text.substring(lastGROPUPBYposition + 8, text.length());
                    staticWhere = text.substring(0, lastGROPUPBYposition);
                }

            }
            if (originalGroupBy.length() > 0) {
                //trovato il groupBy devo scomportlo in groupBy e orderBy
                int lastORDERBYposition = 0;
                word = "ORDER BY";
                text = originalGroupBy;
                for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; i++) {
                    lastORDERBYposition = i;
                }
                if (lastORDERBYposition <= 0) {
                    // non ci sono orderby
                    originalGroupBy = text;
                } else {
                    //c'è un orderby da considerare dopo il where
                    originalOrderBy = text.substring(lastORDERBYposition + 8, text.length());
                    originalGroupBy = text.substring(0, lastORDERBYposition);
                }
            }

            String newFilter = "";
            if (staticWhere.length() > 0) {
                staticWhere = " " + staticWhere.trim() + " ";
            } else {
                staticWhere = "";
            }

            if (keepOriginalWhere == true && staticWhere.length() > 3) {
                // unisco staticWhere e myWhereClause 

                if (myWhereClause != null && myWhereClause.length() > 0) {
                    newFilter = "(" + staticWhere + ") " + joinWhereOrAnd + " (" + myWhereClause + ") ";
                } else {
                    newFilter = "(" + staticWhere + ") ";
                }

            } else {
                // tengo solo myWhereClause 
                
                 if (myWhereClause != null && myWhereClause.length() > 0) {
                      newFilter = " " + myWhereClause;
                }  
              
                
                
            }

            String newGroupby = "";
            String newOrderBy = "";
            String smartTail = "";
            if (myGroupby != null && myGroupby.length() > 0) {
                newGroupby = " GROUP BY " + myGroupby + " ";

            }
            if (originalGroupBy.length() > 0 && keepOriginalGroupby == true) {
                if (newGroupby.length() > 0) {
                    newGroupby += ",";
                } else {
                    newGroupby = " GROUP BY ";
                }
                newGroupby += originalGroupBy;
            }
            
            
//            System.out.println("----SMART--- myOrderBy:" + myOrderBy);
            
            if (myOrderBy != null && myOrderBy.length() > 0) {
                newOrderBy = " ORDER BY " + myOrderBy + " "; 
            }
//            System.out.println("----SMART--- newOrderBy:" + newOrderBy);
            
            if (originalOrderBy.length() > 0 && keepOriginalOrderby == true) {
                if (newOrderBy.length() > 0) {
                    newOrderBy += ",";
                } else {
                    newOrderBy = " ORDER BY ";
                }
                newOrderBy += originalOrderBy;
            }

//            System.out.println("----SMART--- originalOrderBy:" + originalOrderBy);
//            System.out.println("----SMART--- originalGroupBy:" + originalGroupBy);
//            System.out.println("----SMART--- staticWhere:" + staticWhere);
//            System.out.println("----SMART--- newFilter:" + newFilter);
            newQuery = smartPartToKeep + " ";
            if (newFilter != null) {
                newFilter = newFilter.trim();
            }
            if (newFilter.length() > 0) {
                newQuery += " WHERE " + newFilter + " ";
            }

//            System.out.println("\n----SMART--- newOrderBy:" + newOrderBy);
//            System.out.println("----SMART--- newGroupby:" + newGroupby);
//            System.out.println("----SMART--- newFilter:" + newFilter);
            smartTail = newGroupby + " " + newOrderBy;
            newQuery += smartTail;
        }

        System.out.println("----SMARTregenerateQuery--- risultante:" + newQuery);

        return newQuery;
    }

}
