# Date & Time

Adama supports four date and time related types:
* date for storing year, month, day
* time for storing hour, minute
* datetime for storing year, month, day, hour, minute, second, ms
* timespan for storing a duration

## Constants
| Type     | Syntax                 | Example                                                          | Notes                                                                      |
|----------|------------------------|------------------------------------------------------------------|----------------------------------------------------------------------------|
| time     | @time $hr:$min         | @time 4:20                                                       | Use military time for PM                                                   |
| date     | @date $year/$mo/$day   | @date 2023/10/31                                                 | Must be valid                                                              |
| timespan | @timespan $count $unit | @timespan 30 min                                                 | units are sec, min, hr, day, week                                          |
| datetime | @datetime "$iso8601"   | @datetime "2023-04-24T17:57:19.802528800-05:00[America/Chicago]" | [ZonedDateTime.parse](https://www.google.com/search?q=ZonedDateTime.parse) |

## Getting the current date/time

| Method                    | Description                               | Result type |
|---------------------------|-------------------------------------------|-------------|
| Time.today()              | Get the current date                      | date        |
| Time.datetime()           | Get the current date and time             | datetime    |
| Time.time()               | Get the current time of day               | time        |
| Time.zone()               | Get the document's time zone              | string      |
| Time.setZone(string zone) | Set the document's time zone              | bool        |
| Time.now()                | Get the current time as a UNIX time stamp | long        |

## Time functions
| Method                                        | Description                                                  | Result type       |
|-----------------------------------------------|--------------------------------------------------------------|-------------------|
| Time.make(int hr, int min)                    | make a time                                                  | maybe&lt;time&gt; |
| Time.extendWithinDay(time t, timespan s)      | add the timespan to the time clamping the result at midnight | time              |
| Time.cyclicAdd(time t, timespan s)            | add the timespan to the time wraping around the clock        | time              |
| Time.toInt(time t)                            | convert the time to an integer                               | int               |
| Time.overlaps(time a, time b, time c, time d) | do the temporal ranges [a,b] and [c,d] overlap               | bool              |

## Date functions
| Method                                                                      | Description                                                                                                              | Result type           |
|-----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|-----------------------|
| Date.make(int yr, int mo, int day)                                          | make a date                                                                                                              | maybe&lt;date&gt;     |
| Date.construct(date dy, time t, double sec, string zone)                    | make a datetime                                                                                                          | maybe&lt;datetime&gt; |
| Date.calendarViewOf(date&nbsp;d)                                            | Get the surrounding month for the given date                                                                             | list&lt;date&gt;      |
| Date.weekViewOf(date d)                                                     | Get the surrounding week for the given date                                                                              | list&lt;date&gt;      |
| Date.neighborViewOf(date d, int days)                                       | Get the neighborhood for the given date inclusively starting $days in the past to $days into the future                  | list&lt;date&gt;      |
| Date.patternOf(bool m, bool tu, bool w, bool th, bool fr, bool sa, bool su) | Convert the week pattern into an integer bitmask                                                                         | int                   |
| Date.satisfiesWeeklyPattern(date d, int pattern)                            | Does the given date align/match the pattern                                                                              | bool                  |
| Date.inclusiveRange(date from, date to)                                     | Inclusively return a list of all dates starting at $from and ending on $to                                               | list&lt;date&gt;      |
| Date.inclusiveRangeSatisfiesWeeklyPattern(date from, date to, int pattern)  | Inclusively return a list of all dates starting at $from and ending on $to that align/match the pattern                  | list&lt;date&gt;      |
| Date.dayOfWeek(date d)                                                      | Get the day of the week (1 = Monday, 7 = Sunday) as an integer                                                           | int                   |
| Date.dayOfWeekEnglish(date d)                                               | Get the day of the week in english                                                                                       | string                |
| Date.monthNameEnglish(date d)                                               | Get the month in english                                                                                                 | string                |
| Date.offsetMonth(date d, int m)                                             | Add/subtract the number of months from the given date                                                                    | date                  |
| Date.offsetDay(date d, int days)                                            | Add/subtract the number of days from the given date                                                                      | date                  |
| Date.periodYearsFractional(date from, date to)                              | Get the number of years between two dates                                                                                | double                |
| Date.periodMonths(date from, date to)                                       | Get the number of months between two dates                                                                               | int                   |
| Date.between(datetime from, datetime to)                                    | Get the time between two datetimes                                                                                       | timespan              |
| Date.format(date, string format, string lang)                               | Format the date for the given format in the given language (time component is midnight at 0 seconds using UTC time zone) | maybe&lt;string&gt;   |
| Date.format(date, string format)                                            | Format the date for the given format using english (time component is midnight at 0 seconds using UTC time zone)         | maybe&lt;string&gt;   |


## Timespan functions
| Method                                      | Description                                                                   | Result type |
|---------------------------------------------|-------------------------------------------------------------------------------|-------------|
| TimeSpan.add(timespan a, timespan b)        | Add the two timespans together, also the + operator works for this            | timespan    |
| TimeSpan.multiply(timespan a, double v)     | Multiply the timespan by the given double, also the + operator works for this | timespan    |
| TimeSpan.seconds(timespan a) or a.seconds() | Return the timespan as seconds                                                | double      |
| TimeSpan.minutes(timespan a) or a.seconds() | Return the timespan as minutes                                                | double      |
| TimeSpan.hours(timespan a) or a.seconds()   | Return the timespan as hours                                                  | double      |

## DateTime functions
| Method                                            | Description                                                            | Result type       |
|---------------------------------------------------|------------------------------------------------------------------------|-------------------|
| Date.future(datetime d, timespan t)               | Get the future datetime by the given timespan                          | datetime          |
| Date.past(datetime d, timespan t)                 | Get the past datetime by the given timespan                            | datetime          |
| Date.date(datetime d)                             | Convert the datetime to a date, throwing away the time                 | date              |
| Date.time(datetime d)                             | Convert the datetime to a time, throwing away the date                 | time              |
| Date.adjustTimeZone(datetime d, String tz)        | Adjust the timezone if the timezone exists                             | maybe&lt;datetime&gt; |
| Date.format(datetime, string format, string lang) | Format the datetime for the given format in the given language         | maybe&lt;string&gt; |
| Date.format(datetime, string format)              | Format the datetime for the given format using english                 | maybe&lt;string&gt; |
| Date.withYear(datetime d, int year)               | Replace the year                                                       | maybe&lt;datetime&gt; |
| Date.withMonth(datetime d, int month)             | Replace the month                                                      | maybe&lt;datetime&gt; |
| Date.withDayOfMonth(datetime d, int day)          | Replace the day of the month                                           | maybe&lt;datetime&gt; |
| Date.withHour(datetime d, int hour)               | Replace the hour                                                       | maybe&lt;datetime&gt; |
| Date.withMinute(datetime d, int minute)           | Replace the minute the month                                           | maybe&lt;datetime&gt; |
| Date.withTime(datetime d, time t)                 | Replace both the hour and minute and zero out seconds and milliseconds | maybe&lt;datetime&gt; |
| Date.truncateDay(datetime)                        | Zero out the day, hour, minute, seconds, milliseconds                  | datetime |
| Date.truncateHour(datetime)                       | Zero out the hour, minute, seconds, milliseconds                       | datetime |
| Date.truncateMinute(datetime)                     | Zero out the minute, seconds, milliseconds                             | datetime |
| Date.truncateSeconds(datetime)                    | Zero out the seconds, milliseconds                                     | datetime |
| Date.truncateMilliseconds(datetime)               | Zero out the milliseconds                                              | datetime |

