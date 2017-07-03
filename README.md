# LuceneIndexXML

Index all Articles from the New York times from 1987 to 2007

The following fields are indexed - 
1. Headline
2. Lead para
3. Full text
4. Publication date
5. Location
6. Person
7. Organization
8. Classifiers

For the headline, lead para and full text, the named entities are identified using the Stanford NER. <br> All the fields are then added to the index and can be searched by providing the right options.
Lucene is used for indexing and searching.

Usage -

lucene-index.jar create indexDirPath dataDirPath <br>
lucene-index.jar search indexDirPath indexName keyword <br>
Index names: fulltext, leadpara, filename, filepath, headline, pubdate, org, person, location, dsk, classifieridx, classifierongen, classifieronmat, classifierontax


