-- SOURCE RSS
INSERT INTO source(name, url) VALUES ('El tiempo economia', 'http://www.eltiempo.com/economia/rss.xml');
INSERT INTO source(name, url) VALUES ('CNN economia', 'http://feeds2.feedburner.com/cnnexpansion/economia');

-- PROPERTY
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data' WHERE name='FILE_SOURCE_PATH';
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data\\Documents\\Original' WHERE name='FILE_ORIGINAL_SOURCE_PATH';
UPDATE property SET value='D:\\RicardoC\\fuzzyclustering\\data\\Documents\\Clean' WHERE name='FILE_CLEAN_SOURCE_PATH';
UPDATE property SET value='KajYug9Z0gnjRz0BrFhQSQ' WHERE name='TWITTER_CONSUMER_KEY';
UPDATE property SET value='eqf3HZBwPPaKBzt9D4aoOdNhWH2391R85utRENLNEE' WHERE name='TWITTER_CONSUMER_SECRET';
UPDATE property SET value='836549780-xZ5AoEG3061UbvWqHee8DhuONJmvRIJjIvnMwsS0' WHERE name='TWITTER_ACCESS_TOKEN';
UPDATE property SET value='XZ8LRynlddi7vvcIFsjuNsLtDsk6jqcoXoOo9LX687A' WHERE name='TWITTER_ACCESS_TOKEN_SECRET';
UPDATE property SET value='10' WHERE name='WEB_SCRAPPING_THREAD_NUMBER';
UPDATE property SET value='10' WHERE name='INVERTED_INDEX_THREAD_NUMBER';


-- SELECT QUERY
SELECT * FROM source;
SELECT * FROM property;
