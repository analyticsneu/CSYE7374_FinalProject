# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html

import pymongo
from scrapy.conf import settings
from scrapy.exceptions import DropItem
from scrapy import log
import json
import requests

class MongoDBPipeline(object):
    def __init__(self):
        print settings['MONGODB_SERVER']
        connection = pymongo.MongoClient(settings['MONGODB_SERVER'],settings['MONGODB_PORT'])
        db = connection[settings['MONGODB_DB']]
        self.collection = db[settings['MONGODB_COLLECTION']]

    def process_item(self, item, spider):
        data = {'temperature':'2.42'}
        print data
        data_json = json.dumps (data)
        payload = {'json_payload': item['excerpt'],'api_key':'my_key'}
        r = requests.get('http://104.200.18.54:8080/receiveClickEventTest?name='+item['title'],data = payload)
        print r
        valid = True
        for data in item:
            if not data:
                valid = False
                raise DropItem("Missing {0}!".format(data))
        if valid:
                self.collection.insert(dict(item))
                log.msg("Question added to MongoDB database!",level = log.DEBUG, spider = spider)
        return item
