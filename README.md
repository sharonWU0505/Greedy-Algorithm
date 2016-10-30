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
We called the least moving cost of a day "min_cost".
Compare those min_costs, the day with less min_cost has higher priority.
### Move Tasks
By the priority between days, check the workload of that day.
If the workload is not overloading, then check the next day.
Else, try to move tasks to their ideal day by their sorting results.
  However, before successfully moving a task, we have to check whether the day we are moving into is available for that task.
  A task will be viewed as an unassigned task if it has nowhere to go.

## Second Stage Assignment
After the fist stage check, there will be some unassigned tasks and some workload of workdays is not satisfied.
So, we have to assign more tasks as possible by splitting them.

1. Sort all the unassigned tasks by their absolute maximum rewards.
2. According to the sorting result, try to assign a task to several days (if it is still under its splitting limit).
3. ...
