/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene.tests.index;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.internal.tests.IndexWriterAccess;
import org.apache.lucene.internal.tests.TestSecrets;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.tests.analysis.MockAnalyzer;
import org.apache.lucene.tests.analysis.MockTokenizer;

/** Helper functions for tests that handles documents */
public class DocHelper {

  private static final IndexWriterAccess INDEX_WRITER_ACCESS = TestSecrets.getIndexWriterAccess();

  public static final FieldType customType;
  public static final String FIELD_1_TEXT = "field one text";
  public static final String TEXT_FIELD_1_KEY = "textField1";
  public static final Field textField1;

  static {
    customType = new FieldType(TextField.TYPE_STORED);
    textField1 = new Field(TEXT_FIELD_1_KEY, FIELD_1_TEXT, customType);
  }

  public static final FieldType TEXT_TYPE_STORED_WITH_TVS;
  public static final String FIELD_2_TEXT = "field field field two text";
  // Fields will be lexicographically sorted.  So, the order is: field, text, two
  public static final int[] FIELD_2_FREQS = {3, 1, 1};
  public static final String TEXT_FIELD_2_KEY = "textField2";
  public static final Field textField2;

  static {
    TEXT_TYPE_STORED_WITH_TVS = new FieldType(TextField.TYPE_STORED);
    TEXT_TYPE_STORED_WITH_TVS.setStoreTermVectors(true);
    TEXT_TYPE_STORED_WITH_TVS.setStoreTermVectorPositions(true);
    TEXT_TYPE_STORED_WITH_TVS.setStoreTermVectorOffsets(true);
    TEXT_TYPE_STORED_WITH_TVS.freeze();
    textField2 = new Field(TEXT_FIELD_2_KEY, FIELD_2_TEXT, TEXT_TYPE_STORED_WITH_TVS);
  }

  public static final FieldType customType3;
  public static final String FIELD_3_TEXT = "aaaNoNorms aaaNoNorms bbbNoNorms";
  public static final String TEXT_FIELD_3_KEY = "textField3";
  public static final Field textField3;

  static {
    customType3 = new FieldType(TextField.TYPE_STORED);
    customType3.setOmitNorms(true);
    textField3 = new Field(TEXT_FIELD_3_KEY, FIELD_3_TEXT, customType3);
  }

  public static final String KEYWORD_TEXT = "Keyword";
  public static final String KEYWORD_FIELD_KEY = "keyField";
  public static final Field keyField;

  static {
    keyField = new StringField(KEYWORD_FIELD_KEY, KEYWORD_TEXT, Field.Store.YES);
  }

  public static final FieldType customType5;
  public static final String NO_NORMS_TEXT = "omitNormsText";
  public static final String NO_NORMS_KEY = "omitNorms";
  public static final Field noNormsField;

  static {
    customType5 = new FieldType(TextField.TYPE_STORED);
    customType5.setOmitNorms(true);
    customType5.setTokenized(false);
    noNormsField = new Field(NO_NORMS_KEY, NO_NORMS_TEXT, customType5);
  }

  public static final FieldType customType6;
  public static final String NO_TF_TEXT = "analyzed with no tf and positions";
  public static final String NO_TF_KEY = "omitTermFreqAndPositions";
  public static final Field noTFField;

  static {
    customType6 = new FieldType(TextField.TYPE_STORED);
    customType6.setIndexOptions(IndexOptions.DOCS);
    noTFField = new Field(NO_TF_KEY, NO_TF_TEXT, customType6);
  }

  public static final FieldType customType7;
  public static final String UNINDEXED_FIELD_TEXT = "unindexed field text";
  public static final String UNINDEXED_FIELD_KEY = "unIndField";
  public static final Field unIndField;

  static {
    customType7 = new FieldType();
    customType7.setStored(true);
    unIndField = new Field(UNINDEXED_FIELD_KEY, UNINDEXED_FIELD_TEXT, customType7);
  }

  public static final FieldType STRING_TYPE_STORED_WITH_TVS;

  static {
    STRING_TYPE_STORED_WITH_TVS = new FieldType(StringField.TYPE_STORED);
    STRING_TYPE_STORED_WITH_TVS.setStoreTermVectors(true);
    STRING_TYPE_STORED_WITH_TVS.setStoreTermVectorPositions(true);
    STRING_TYPE_STORED_WITH_TVS.setStoreTermVectorOffsets(true);
    STRING_TYPE_STORED_WITH_TVS.freeze();
  }

  public static final String UNSTORED_1_FIELD_TEXT = "unstored field text";
  public static final String UNSTORED_FIELD_1_KEY = "unStoredField1";
  public static final Field unStoredField1 =
      new TextField(UNSTORED_FIELD_1_KEY, UNSTORED_1_FIELD_TEXT, Field.Store.NO);

  public static final FieldType customType8;
  public static final String UNSTORED_2_FIELD_TEXT = "unstored field text";
  public static final String UNSTORED_FIELD_2_KEY = "unStoredField2";
  public static final Field unStoredField2;

  static {
    customType8 = new FieldType(TextField.TYPE_NOT_STORED);
    customType8.setStoreTermVectors(true);
    unStoredField2 = new Field(UNSTORED_FIELD_2_KEY, UNSTORED_2_FIELD_TEXT, customType8);
  }

  public static final String LAZY_FIELD_BINARY_KEY = "lazyFieldBinary";

  @SuppressWarnings("NonFinalStaticField")
  public static byte[] LAZY_FIELD_BINARY_BYTES;

  @SuppressWarnings("NonFinalStaticField")
  public static Field lazyFieldBinary;

  public static final String LAZY_FIELD_KEY = "lazyField";
  public static final String LAZY_FIELD_TEXT = "These are some field bytes";
  public static final Field lazyField = new Field(LAZY_FIELD_KEY, LAZY_FIELD_TEXT, customType);

  public static final String LARGE_LAZY_FIELD_KEY = "largeLazyField";

  @SuppressWarnings("NonFinalStaticField")
  public static String LARGE_LAZY_FIELD_TEXT;

  @SuppressWarnings("NonFinalStaticField")
  public static Field largeLazyField;

  // From Issue 509
  public static final String FIELD_UTF1_TEXT = "field one \u4e00text";
  public static final String TEXT_FIELD_UTF1_KEY = "textField1Utf8";
  public static final Field textUtfField1 =
      new Field(TEXT_FIELD_UTF1_KEY, FIELD_UTF1_TEXT, customType);

  public static final String FIELD_UTF2_TEXT = "field field field \u4e00two text";
  // Fields will be lexicographically sorted.  So, the order is: field, text, two
  public static final int[] FIELD_UTF2_FREQS = {3, 1, 1};
  public static final String TEXT_FIELD_UTF2_KEY = "textField2Utf8";
  public static final Field textUtfField2 =
      new Field(TEXT_FIELD_UTF2_KEY, FIELD_UTF2_TEXT, TEXT_TYPE_STORED_WITH_TVS);

  public static final Map<String, Object> nameValues;

  // ordered list of all the fields...
  // could use LinkedHashMap for this purpose if Java1.4 is OK
  public static final Field[] fields =
      new Field[] {
        textField1,
        textField2,
        textField3,
        keyField,
        noNormsField,
        noTFField,
        unIndField,
        unStoredField1,
        unStoredField2,
        textUtfField1,
        textUtfField2,
        lazyField,
        // placeholder for binary field, since this is null.  It must be second to last.
        lazyFieldBinary,
        // placeholder for large field, since this is null.  It must always be last
        largeLazyField
      };

  public static final Map<String, IndexableField> all = new HashMap<>();
  public static final Map<String, IndexableField> indexed = new HashMap<>();
  public static final Map<String, IndexableField> stored = new HashMap<>();
  public static final Map<String, IndexableField> unstored = new HashMap<>();
  public static final Map<String, IndexableField> unindexed = new HashMap<>();
  public static final Map<String, IndexableField> termvector = new HashMap<>();
  public static final Map<String, IndexableField> notermvector = new HashMap<>();
  public static final Map<String, IndexableField> lazy = new HashMap<>();
  public static final Map<String, IndexableField> noNorms = new HashMap<>();
  public static final Map<String, IndexableField> noTf = new HashMap<>();

  static {
    // Initialize the large Lazy Field
    String buffer = "Lazily loading lengths of language in lieu of laughing ".repeat(10000);

    LAZY_FIELD_BINARY_BYTES = "These are some binary field bytes".getBytes(StandardCharsets.UTF_8);
    lazyFieldBinary = new StoredField(LAZY_FIELD_BINARY_KEY, LAZY_FIELD_BINARY_BYTES);
    fields[fields.length - 2] = lazyFieldBinary;
    LARGE_LAZY_FIELD_TEXT = buffer;
    largeLazyField = new Field(LARGE_LAZY_FIELD_KEY, LARGE_LAZY_FIELD_TEXT, customType);
    fields[fields.length - 1] = largeLazyField;
    for (IndexableField f : fields) {
      add(all, f);
      if (f.fieldType().indexOptions() != IndexOptions.NONE) add(indexed, f);
      else add(unindexed, f);
      if (f.fieldType().storeTermVectors()) add(termvector, f);
      if (f.fieldType().indexOptions() != IndexOptions.NONE && !f.fieldType().storeTermVectors())
        add(notermvector, f);
      if (f.fieldType().stored()) add(stored, f);
      else add(unstored, f);
      if (f.fieldType().indexOptions() == IndexOptions.DOCS) add(noTf, f);
      if (f.fieldType().omitNorms()) add(noNorms, f);
      if (f.fieldType().indexOptions() == IndexOptions.DOCS) add(noTf, f);
      // if (f.isLazy()) add(lazy, f);
    }
  }

  private static void add(Map<String, IndexableField> map, IndexableField field) {
    map.put(field.name(), field);
  }

  static {
    nameValues = new HashMap<>();
    nameValues.put(TEXT_FIELD_1_KEY, FIELD_1_TEXT);
    nameValues.put(TEXT_FIELD_2_KEY, FIELD_2_TEXT);
    nameValues.put(TEXT_FIELD_3_KEY, FIELD_3_TEXT);
    nameValues.put(KEYWORD_FIELD_KEY, KEYWORD_TEXT);
    nameValues.put(NO_NORMS_KEY, NO_NORMS_TEXT);
    nameValues.put(NO_TF_KEY, NO_TF_TEXT);
    nameValues.put(UNINDEXED_FIELD_KEY, UNINDEXED_FIELD_TEXT);
    nameValues.put(UNSTORED_FIELD_1_KEY, UNSTORED_1_FIELD_TEXT);
    nameValues.put(UNSTORED_FIELD_2_KEY, UNSTORED_2_FIELD_TEXT);
    nameValues.put(LAZY_FIELD_KEY, LAZY_FIELD_TEXT);
    nameValues.put(LAZY_FIELD_BINARY_KEY, LAZY_FIELD_BINARY_BYTES);
    nameValues.put(LARGE_LAZY_FIELD_KEY, LARGE_LAZY_FIELD_TEXT);
    nameValues.put(TEXT_FIELD_UTF1_KEY, FIELD_UTF1_TEXT);
    nameValues.put(TEXT_FIELD_UTF2_KEY, FIELD_UTF2_TEXT);
  }

  /**
   * Adds the fields above to a document
   *
   * @param doc The document to write
   */
  public static void setupDoc(Document doc) {
    for (Field field : fields) {
      doc.add(field);
    }
  }

  /**
   * Writes the document to the directory using a segment named "test"; returns the SegmentInfo
   * describing the new segment
   */
  public static SegmentCommitInfo writeDoc(Random random, Directory dir, Document doc)
      throws IOException {
    return writeDoc(dir, new MockAnalyzer(random, MockTokenizer.WHITESPACE, false), null, doc);
  }

  /**
   * Writes the document to the directory using the analyzer and the similarity score; returns the
   * SegmentInfo describing the new segment
   */
  public static SegmentCommitInfo writeDoc(
      Directory dir, Analyzer analyzer, Similarity similarity, Document doc) throws IOException {
    IndexWriter writer =
        new IndexWriter(
            dir,
            new IndexWriterConfig(
                    /* LuceneTestCase.newIndexWriterConfig(random, */
                    analyzer)
                .setSimilarity(
                    similarity == null ? IndexSearcher.getDefaultSimilarity() : similarity));
    // writer.setNoCFSRatio(0.0);
    writer.addDocument(doc);
    writer.commit();
    SegmentCommitInfo info = INDEX_WRITER_ACCESS.newestSegment(writer);
    writer.close();
    return info;
  }

  public static int numFields(Document doc) {
    return doc.getFields().size();
  }

  public static Document createDocument(int n, String indexName, int numFields) {
    StringBuilder sb = new StringBuilder();

    final Document doc = new Document();
    doc.add(new Field("id", Integer.toString(n), STRING_TYPE_STORED_WITH_TVS));
    doc.add(new Field("indexname", indexName, STRING_TYPE_STORED_WITH_TVS));
    sb.append("a");
    sb.append(n);
    doc.add(new Field("field1", sb.toString(), TEXT_TYPE_STORED_WITH_TVS));
    sb.append(" b");
    sb.append(n);
    for (int i = 1; i < numFields; i++) {
      doc.add(new Field("field" + (i + 1), sb.toString(), TEXT_TYPE_STORED_WITH_TVS));
    }
    return doc;
  }
}
