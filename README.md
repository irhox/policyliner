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

## Set-up
PolicyLiner is prepared and tested to work with PostgreSQL. It should work with other types of relational databases, but this has not been tested yet.
To evaluate PolicyLiner, we used the datasets by Kartoun et al. that can be found in the following links depending on the sizes [100-Patients](https://huggingface.co/datasets/kartoun/EMRBots_100_patients), [10000-Patients](https://huggingface.co/datasets/kartoun/EMRBots_10000_patients), [100000-Patients](https://huggingface.co/datasets/kartoun/EMRBots_100000_patients/tree/main). 

To emulate our experiments, one needs to:
1. Adapt one of these datasets into a PostgreSQL database
2. Run the data masking functions from the Mascara repository and our repository defined in: <em>policyliner/src/main/resources/maskingFunctions</em>.
3. Create the disclosure policies, defined in folder: <em>policyliner/src/main/resources/policyCreationQueries</em>.
4. Add the following environment variables:
   
   DATA_DB_KIND=postgresql;
   
   DATA_DB_PASSWORD=

   DATA_DB_URL=

   DATA_DB_USER=

   PL_DB_KIND=postgresql;

   PL_DB_PASSWORD=

   PL_DB_URL=

   PL_DB_USER=
   
6. Install the Quarkus CLI using Chocolatey with: <em>choco install quarkus</em>.
7. Run PolicyLiner backend command: <em>quarkus dev</em>
8. Run PolicyLiner DemoUI: <em>npm install; npm start</em>

For more detailed info check the [Master Thesis Paper](Abschlussarbeit_487673.pdf).
