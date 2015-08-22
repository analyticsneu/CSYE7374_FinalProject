from stack.items import StackItem
from scrapy.selector import Selector
from scrapy.contrib.linkextractors import LinkExtractor
from scrapy.contrib.spiders import CrawlSpider, Rule

class StackSpider (CrawlSpider):
    name = "stack"
    allowed_domains = ["stackoverflow.com"]
    start_urls = [
          'http://stackoverflow.com/questions?page=5001&sort=newest'
    ]
    rules = [
         Rule(LinkExtractor(allow=r'questions\?page=[5-9][0-9][0-9][0-9]&sort=newest'),callback='parse_item', follow=True),
    ]
    def parse_item(self, response):
        questions = Selector(response).xpath('//div[@class="question-summary"]')
        for question in questions:
            item = StackItem ()
            item['vote_count_post'] = question.xpath('.//div[@class="votes"]//strong/text()').extract()[0]
            if len(question.xpath('.//div[@class="status answered"]//strong/text()').extract()) > 0:
                item['answers_num'] = question.xpath('.//div[@class="status answered"]//strong/text()').extract()[0]
            else:
                item['answers_num'] = "0"
            if len(question.xpath('.//div[@class="views "]/@title').extract()) > 0:
                item['views'] = question.xpath('.//div[@class="views "]/@title').extract()[0][:-5].strip()
            else:
                item['views'] = "0"
            item['excerpt'] = question.xpath('.//div[@class="excerpt"]/text()').extract()[0].strip()
            item['tags'] = question.xpath('.//a[@class="post-tag"]/text()').extract()
            item['title'] = question.xpath(
                './/h3/a[@class="question-hyperlink"]/text()').extract()[0]
            item['url'] = question.xpath(
                './/h3/a[@class="question-hyperlink"]/@href').extract()[0]
            yield item
