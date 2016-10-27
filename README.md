# Greedy-Algorithm

## First Stage Assignment
Assign the task to its ideal day.
Ideal day is the day when the absolute maximum rewards of a task exists.

## First Stage Check
After the fist stage assignment, the workload of some days may be overloading.
So, we have to do some changes.

Check from the first workdays.
If the workload is not overloading, then check the next workday.
Else,
  1. Sort all the tasks on that day by "their cost to move to another workday".
  2. According to the sorting result, try to move the task with the least moving cost.
  3. ...

## Second Stage Assignment
After the fist stage check, there will be some unassigned tasks and some workload of workdays is not satisfied.
So, we have to assign more tasks as possible by splitting them.

1. Sort all the unassigned tasks by their absolute maximum rewards.
2. According to the sorting result, try to assign a task to several days (if it is still under its splitting limit).
3. ...
