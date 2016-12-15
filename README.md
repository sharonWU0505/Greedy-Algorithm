# Greedy-Algorithm

## First Stage Assignment
Assign each task to its ideal day.
Ideal day is the day when the absolute maximum rewards of a task exists.

## First Stage Check
After the first stage assignment, the workload of some days may be overloading.
So, we have to do some changes.

### Sort Tasks
For each day, sort tasks by "the cost to move that task to another day".
### Decide Priority Between Days
We called the least moving cost of a task in a day the "min_cost" of that day.
Compare those min_costs, the day with less min_cost has higher priority.
Arrange the schedule in the order of the priority.
	Put a tag at the first element of each tasks-list to identify the day which it belongs to. 
	Recover the format to the origin one after finishing the First Stage Check.
### Move Tasks
By the priority between days, check the sum of processing time and traveling time of that day.  
If the workload is not overloading, check the next day.  
Else, try to move tasks to their ideal day by their sorting results.   
  However, before successfully moving a task, we have to check whether the day we are moving into is available for that task.   
  A task will be viewed as an unassigned task if it has nowhere to go.   

## Second Stage Assignment
After the first stage check, there will be some unassigned tasks and some work days with unfilled workloads.
So, we try to assign as more tasks as possible by splitting them.

1. Sort all the unassigned tasks by their "potential net rewards".
	a potential net rewards = (max_rewards * the proportion the task may be done on the ideal day) - split cost 
2. According to the sorting result, try to assign a task to the day that it can earn the maximum potential net rewards.
	If the potential net rewards is negative, abandon the task.
	Meanwhile, a task should not be over-split.
3. If the task is not completely done, calculate its new potential net rewards according to the proportion left unfinished.
4. If the ideal day has no enough capacity for the task, re-calculate its potential net rewards while avoiding assigning it to the busy days.
5. Go back to 2.

## Task Sequence
After deciding the tasks to-do in each days, we apply the method of exhaustion to get the task sequence which has the minimum traveling time. 

