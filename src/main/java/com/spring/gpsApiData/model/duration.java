package com.spring.gpsApiData.model;

import lombok.Data;

@Data
public class duration {
	private long seconds;
	private long minutes;
	private long hours;
	private long days;

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if(this.days > 0)
		{
			sb.append(days);
			if(this.days == 1)
			{
				sb.append("day" + " ");
			}
			else
			{				
				sb.append("days" + " ");
			}
		}
		if(this.hours > 0)
		{
			sb.append(hours);
			if(this.hours == 1)
			{
				sb.append("hour" + " ");
			}
			else
			{				
				sb.append("hours" + " ");
			}
		}
		if(this.minutes > 0)
		{
			sb.append(minutes);
			sb.append("minutes" + " ");
		}
		if(this.seconds > 0)
		{
			sb.append(seconds);
			sb.append("seconds" + " ");
		}
		return sb.toString();
	}
}
