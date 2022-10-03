# SafeBySafe II Android

SafeBySafe is the fork of the Element Android, the Matrix Client provided by [Element](https://element.io/) and adjusted for simplified sign-in and work
in the SafeBySafe family matrix network.

The main changes made are:

- Home server is hard-set to SafeBySafe network and any controls setting/changing it are hidden from the end user.
- Removed option to sign-in to existing account - if user looses his phone or data - new account has to be created by SafeBySafe policies.
- Removed UI access to spaces to avoid confusing old fashioned SafeBySafe users
- Removed some tech-y settings for the same reason
- Introduced the local address (contacts) book to behave like contacts list in other messengers
- Removed/disabled analytics/etc
- Removed/disabled general use intro/opt-in screens

IMPORTANT: This is not the general use Matrix client for everyone - it does not allow to connect to arbitrary home server. This is just a UX-simplified fork
of the great [Element](https://element.io/) app to address the needs of SafeBySafe users community which uses their private Matrix server for safer communication.
