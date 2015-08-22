from flask import Blueprint
main = Blueprint('main', __name__)

from flask import Flask, request

import logging

import json

from music_reco_engine import RecommendationEngine
 


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
 

 
 
@main.route("/<int:userid>/ratings/top/<int:count>", methods=["GET"])
def top_ratings(userid, count):
    logger.debug("User %s TOP ratings requested", userid)
    top_ratings = recommendation_engine.get_top_ratings(userid,count)
    return json.dumps(top_ratings)
 
@main.route("/<int:userid>/ratings/<int:musicid>", methods=["GET"])
def music_ratings(userid, musicid):
    logger.debug("User %s rating requested for music %s", userid, musicid)
    ratings = recommendation_engine.get_ratings_for_music_ids(userid, [musicid])
    return json.dumps(ratings)
 
 
@main.route("/<int:user_id>/ratings", methods = ["POST"])
def add_ratings(userid):
	ratingslist = request.form.keys()[0].strip().split("\n") #getting the ratings from the Flask POST request object
    	ratingslist = map(lambda x: x.split(","), ratingslist)
    
    	ratings = map(lambda x: (userid, int(x[0]), float(x[1])), ratingslist) #create a list with the format required by the negine (userid, musicid, rating)
    
   	recommendation_engine.add_ratings(ratings) # add them to the model using then engine API
 
   	return json.dumps(ratings)
 
 
def create_app(spark_context, dataset_path):
    global recommendation_engine 

    recommendation_engine = RecommendationEngine(spark_context, dataset_path)    
    
    app = Flask(__name__)
    app.register_blueprint(main)
    return app