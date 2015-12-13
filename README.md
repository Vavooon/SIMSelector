# SIMSelector
Xposed module which automatically selects SIM card using text filters when you make new call. Works with all dialers.

## Features
Module uses text filters to determine a right SIM card for outgoing call.

You can use +, 0-9 digits and next sequences:

* Brackets with char sequences inside splitted by commas. For example, (63,73,93) will match numbers with all specefied sequences.
* Sharp(#) char will mach any char



For example, if you need to call from desired SIM to numbers with operator codes 63,73,93 and your country code is +380 you have to add a rule like:

+380(63,73,93)#######

In case you do not store your phone numbers with country codes you can use rule like

(+380,380,80,0)(63,73,93)#######

It will match both numbers with country code ahead and phone numbers without them:

063xxxxxxx

8063xxxxxxx

## Requirments

* Android 6.0
* Xposed 78
* Phone which uses standard Multi-SIM API
