# SMART（Schedule-Making Algorithm with Reassignment and Travelling）

## ● First-Stage Assignment
Assign each task to its ideal day.
The ideal day of a task is the day when the maximum rewards exists.

## ● First-Stage Check
After the First-Stage assignment, some days may be overloading.
So, we have to do some changes by either moving some tasks or not assigning some tasks.

#### ◎ Decide Priority Between Days
We called the least moving cost of any task in a day the "min_cost" of that day.
Compare those min_costs, a day with less min_cost has higher priority.
#### ◎ Sort Tasks
For each day, sort tasks by "the cost to move that task to another day".
#### ◎ Move Tasks
We call the sum of processing time and traveling time of a day "total time".
By the priority between days, check if the total time of a day is out of limitation.  
If a day is not overloading, check the next day.  
Else, try to move tasks to another day by their sorting results.   
  However, before really moving a task, we have to check whether the day we are moving into is available for that task.   
  A task will be viewed as an unassigned task if it has nowhere to go.   

## ● Second Stage Assignment
After the First-Stage check, there will be some unassigned tasks and some days with unfilled workload.
So, we try to assign as more tasks as possible by splitting them.

#### ◎ Sort Tasks
Sort all the unassigned tasks by their "potential net rewards" to get a waiting list.
potential net rewards = (max_rewards * the proportion a task can be assigned on a day) - split cost 
#### ◎ Assign Splitted Tasks
According to the waiting list, assign a task to the day that it can earn the maximum potential net rewards.
If the day is not available, re-calculate the potential net rewards of that task by assigning it to another day. 
If the task is not completely assigned, make the proportion left a new task and make it into the waiting list.
Keep doing those things until no day has time left or no more potential net rewards.

## ● Decide Task Sequence
After deciding the tasks to-do in each day, we get the task sequence with the minimum traveling time by exhaustion. 
