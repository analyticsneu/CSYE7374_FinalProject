import datetime
import time
import os
from apscheduler.scheduler import Scheduler

# Start the scheduler
sched = Scheduler()
sched.daemonic = False
sched.start()

def job_function():
    os.system(" spark-submit --class ClimateClustering --master local[4] CSYE7374_Final_Spark-assembly-1.0.jar ec2-52-20-252-81.compute-1.amazonaws.com")
    os.system(" spark-submit --class ClimateARIMA --master local[4] CSYE7374_Final_Spark-assembly-1.0.jar ec2-52-20-252-81.compute-1.amazonaws.com")
    print(datetime.datetime.now())
    time.sleep(20)

# Schedules job_function to be run once each minute
sched.add_job(job_function, "cron", hour='0', minute='0',second='0')

