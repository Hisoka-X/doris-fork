// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.analysis;

import org.apache.doris.catalog.Column;
import org.apache.doris.catalog.ScalarType;
import org.apache.doris.common.AnalysisException;
import org.apache.doris.common.Config;
import org.apache.doris.common.UserException;
import org.apache.doris.qe.ShowResultSetMetaData;

/**
 * Statement for show all catalog or desc the specific catalog.
 */
public class ShowCatalogStmt extends ShowStmt {
    private static final ShowResultSetMetaData META_DATA_ALL =
            ShowResultSetMetaData.builder()
                    .addColumn(new Column("CatalogName", ScalarType.createVarchar(64)))
                    .addColumn(new Column("Type", ScalarType.createStringType()))
                    .build();

    private static final ShowResultSetMetaData META_DATA_SPECIFIC =
            ShowResultSetMetaData.builder()
                    .addColumn(new Column("Key", ScalarType.createStringType()))
                    .addColumn(new Column("Value", ScalarType.createStringType()))
                    .build();

    private final String catalogName;

    public ShowCatalogStmt(String catalogName) {
        this.catalogName = catalogName;
    }

    public ShowCatalogStmt() {
        this.catalogName = null;
    }

    public String getCatalogName() {
        return catalogName;
    }

    @Override
    public void analyze(Analyzer analyzer)  throws AnalysisException, UserException {
        if (!Config.enable_multi_catalog) {
            throw new AnalysisException("The multi-catalog feature is still in experiment, and you can enable it "
                    + "manually by set fe configuration named `enable_multi_catalog` to be ture.");
        }
        super.analyze(analyzer);
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("SHOW");

        if (catalogName != null) {
            sb.append(" CATALOG ");
            sb.append(catalogName);
        } else {
            sb.append(" CATALOGS");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toSql();
    }

    @Override
    public ShowResultSetMetaData getMetaData() {
        if (catalogName == null) {
            return META_DATA_ALL;
        } else {
            return META_DATA_SPECIFIC;
        }
    }
}
