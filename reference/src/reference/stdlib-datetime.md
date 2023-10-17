# Date & Time

Adama supports four date and time related types:
* date for storing year, month, day
* time for storing hour, minute
* datetime for storing year, month, day, hour, minute, second, ms
* timespan for storing a duration

## Getting the current date/time

| Method                    | Description                               | Result type |
|---------------------------|-------------------------------------------|-------------|
| Time.today()              | Get the current date                      | date        |
| Time.datetime()           | Get the current date and time             | datetime    |
| Time.time()               | Get the current time of day               | time        |
| Time.zone()               | Get the document's time zone              | string      |
| Time.setZone(string zone) | Set the document's time zone              | bool        |
| Time.now()                | Get the current time as a UNIX time stamp | long        |


## Date functions
| Method                                                                      | Description                                                                                             | Result type      |
|-----------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|------------------|
| Date.calendarViewOf(date&nbsp;d)                                            | Get the surrounding month for the given date                                                            | list&lt;date&gt; |
| Date.weekViewOf(date d)                                                     | Get the surrounding week for the given date                                                             | list&lt;date&gt; |
| Date.neighborViewOf(date d, int days)                                       | Get the neighborhood for the given date inclusively starting $days in the past to $days into the future | list&lt;date&gt; |
| Date.patternOf(bool m, bool tu, bool w, bool th, bool fr, bool sa, bool su) | Convert the week pattern into an integer bitmask                                                        | int              |
| Date.satisfiesWeeklyPattern(date d, int pattern)                            | Does the given date align/match the pattern                                                             | bool             |
| Date.inclusiveRange(date from, date to)                                     | Inclusively return a list of all dates starting at $from and ending on $to                              | list&lt;date&gt; |
| Date.inclusiveRangeSatisfiesWeeklyPattern(date from, date to, int pattern)  | Inclusively return a list of all dates starting at $from and ending on $to that align/match the pattern | list&lt;date&gt; |
| Date.dayOfWeek(date d)                                                      | Get the day of the week (1 = Monday, 7 = Sunday) as an integer                                          | int              |
| Date.dayOfWeekEnglish(date d)                                               | Get the day of the week in english                                                                      | string           |
| Date.monthNameEnglish(date d)                                               | Get the month in english                                                                                | string           |
| Date.offsetMonth(date d, int m)                                             | Add/subtract the number of months from the given date                                                   | date             |
| Date.offsetDay(date d, int days)                                            | Add/subtract the number of days from the given date                                                     | date             |
| Date.periodYearsFractional(date from, date to)                              | Get the number of years between two dates                                                               | double           |
| Date.periodMonths(date from, date to)                                       | Get the number of months between two dates                                                              | int              |


## DateTime functions

| Method                                            | Description                                                    | Result type           |
|---------------------------------------------------|----------------------------------------------------------------|-----------------------|
| Date.future(datetime d, timespan t)               | Get the future datetime by the given timespan                  | datetime              |
| Date.past(datetime d, timespan t)                 | Get the past datetime by the given timespan                    | datetime              |
| Date.date(datetime d)                             | Convert the datetime to a date, throwing away the time         | date                  |
| Date.time(datetime d)                             | Convert the datetime to a time, throwing away the date         | time                  |
| Date.adjustTimeZone(datetime d, String tz)        | Adjust the timezone if the timezone exists                     | maybe&lt;datetime&gt; |
| Date.format(datetime, string format, string lang) | Format the datetime for the given format in the given language | maybe&lt;string&gt;   |
| Date.format(datetime, string format)              | Format the datetime for the given format using english         | maybe&lt;string&gt;   |

