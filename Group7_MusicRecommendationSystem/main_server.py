
import time
import os
import sys
import cherrypy
from paste.translogger import TransLogger
from pyspark import SparkContext, SparkConf
from flask_application import create_app


def init_spark_context():
    conf = SparkConf().setAppName("music_recommendation_server")
    sc = SparkContext(conf=conf, pyFiles=['music_reco_engine.py', 'flask_application.py'])
    return sc
 
 
def start_server(y):
	x = TransLogger(y) #WSGI access logging enabler
    	cherrypy.tree.graft(x, '/')
 
    #Set the configuration of the web server
    	cherrypy.config.update({
        'engine.autoreload.on': True,
        'log.screen': True,
        'server.socket_port': 5432,
        'server.socket_host': '0.0.0.0'
    	})
    	cherrypy.engine.start() #starting CherryPy WSGI web server
    	cherrypy.engine.block()
 
 
if __name__ == "__main__":
    sc = init_spark_context()
    dataset_path = os.path.join('datasets', 'ml-latest')
    application = create_app(sc, dataset_path)
 
    # start web server
    start_server(application)