# Greedy-Algorithm

## First Stage Assignment
Assign each task to its ideal day.
Ideal day is the day when the absolute maximum rewards of a task exists.

## First Stage Check
After the fist stage assignment, the workload of some days may be overloading.
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
By the priority between days, check the workload of that day.  
If the workload is not overloading, then check the next day.  
Else, try to move tasks to their ideal day by their sorting results.   
  However, before successfully moving a task, we have to check whether the day we are moving into is available for that task.   
  A task will be viewed as an unassigned task if it has nowhere to go.   

## Second Stage Assignment
After the fist stage check, there will be some unassigned tasks and some workload of workdays is not satisfied.
So, we have to assign more tasks as possible by splitting them.

1. Sort all the unassigned tasks by their "potential net rewards".
	potential net rewards = (max_rewards * the proportion the task may be done on the ideal day) - split cost 
2. According to the sorting result, try to assign a task to the day that it can earn the maximum potential net rewards.
	If the potential net rewards is negative, abandon the task.
	A task should not be over-split.
3. If the task is not completely done, calculate its new potential net rewards according to the proportion left unfinished.
4. If the ideal day for a task is out of capacity, re-calculate its potential net rewards by avoiding assigning it to the busy days.
5. Go back to 2.

