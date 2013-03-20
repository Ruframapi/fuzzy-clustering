-- SOURCE RSS
DELETE from source;
INSERT INTO source(name, url, type) VALUES ('El tiempo economia', 'http://www.eltiempo.com/economia/rss.xml', 'RSS');
INSERT INTO source(name, url, type) VALUES ('CNN economia', 'http://feeds2.feedburner.com/cnnexpansion/economia', 'RSS');

-- PROPERTY
-- INSERT INTO property (name,value) VALUES('WEB_SCRAPPING_RSS_DOWNLOAD_TIME','10'); 
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data' WHERE name='FILE_SOURCE_PATH';
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data\\Documents\\InvertedIndex' WHERE name='FILE_INVERTED_INDEX';
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data\\Documents\\Original' WHERE name='FILE_ORIGINAL_PATH';
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data\\Documents\\Clean' WHERE name='FILE_CLEAN_PATH';
UPDATE property SET value='KajYug9Z0gnjRz0BrFhQSQ' WHERE name='TWITTER_CONSUMER_KEY';
UPDATE property SET value='eqf3HZBwPPaKBzt9D4aoOdNhWH2391R85utRENLNEE' WHERE name='TWITTER_CONSUMER_SECRET';
UPDATE property SET value='836549780-xZ5AoEG3061UbvWqHee8DhuONJmvRIJjIvnMwsS0' WHERE name='TWITTER_ACCESS_TOKEN';
UPDATE property SET value='XZ8LRynlddi7vvcIFsjuNsLtDsk6jqcoXoOo9LX687A' WHERE name='TWITTER_ACCESS_TOKEN_SECRET';
UPDATE property SET value='10' WHERE name='WEB_SCRAPPING_THREAD_NUMBER';
UPDATE property SET value='10' WHERE name='INVERTED_INDEX_THREAD_NUMBER';
UPDATE property SET value=' ' WHERE name='TEXT_SPLIT_TOKEN';
UPDATE property SET value='true' WHERE name='TEXT_STEMMER_ON';
UPDATE property SET value='10' WHERE name='TWITTER_HOME_TIMELINE_PAGES';


--EXPRESIONES REGULARES
DELETE FROM stopword;
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Numeros','[0-9]+\\.*\\,*[0-9]*','','0',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Caracteres Especiales','[:()¿$-%“”;]',' ','1',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Caracteres Especiales (Escape)','[\\\\,\\\\.\\\\\"\\\\\']',' ','2',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Cambio Interogación por punto','[?]','.','3',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Saltos de Linea','[\\r\\n]','','4',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Etiquetas HTML','<.*?>','','5',true);
INSERT INTO stopword (name,regex,regexreplace,stopwordorder,enabled) VALUES ('Dos o mas espacios a espacio sencillo','\\s{2,}',' ','6',true);



-- SELECT QUERY
SELECT * FROM source;
SELECT * FROM property;
