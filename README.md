# PolicyLiner: A Disclosure-compliant Database Auditing Tool

Modern data-intensive systems face growing, ongoing challenges in achieving and
maintaining compliance with privacy regulations such as GDPR, especially when
dealing with dynamic datasets. While systems like Mascara can enforce disclosure
policies during query execution, they lack mechanisms to ensure these policies
remain effective over time or resilient against vulnerabilities introduced by evolving, dynamic data. This thesis addresses the problem of assessing and preserving
the privacy guarantees of disclosure-compliant policies and queries in relational
database environments with continuously changing data.

To solve this, we design and implement PolicyLiner, a database auditing tool
that combines online and offline auditing to monitor and continuously evaluate
disclosure policies and disclosure queries. The online auditing process performs
real-time query analysis to detect suspicious patterns that can lead to vulnerabilities towards query replay attacks, while the offline auditing process retrospectively
examines historical queries and evaluates disclosure policies using various privacy
metrics, including t-Closeness, δ-Presence, Sample Uniqueness Ratio, and Population Uniqueness Estimation. Through a comprehensive experimental evaluation,
we demonstrate that PolicyLiner effectively identifies multiple types of privacy attacks, maintains low query execution overhead, and scales under concurrent workloads. Our results show that PolicyLiner can discover vulnerabilities to various
privacy attacks, such as Membership Disclosure Attacks, Attribute Disclosure Attacks, Re-identification Attacks, and Query Replay Attacks, giving the data officer
the needed tools and foresight to prevent these attacks on time and significantly
strengthen the long-term robustness of sensitive database systems by ensuring that
disclosure-compliant policies remain accurate and up-to-date.
