<!--
  Copyright 1999-2004 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- ===================================================================

     Apache Faq module (Version 2.0)

TYPICAL INVOCATION:

  <!ENTITY % faq PUBLIC
      "-//APACHE//ENTITIES FAQ Vxy//EN"
      "faq-vxy.mod">
  %faq;

  where

    x := major version
    y := minor version

NOTES:

FIXME:

CHANGE HISTORY:
[Version 2.0a]
  20030506 Changed <part> to <faqsection>
  20030506 Changed @title on <faqs> to a nested <title> element

==================================================================== -->

<!-- =============================================================== -->
<!-- Element declarations -->
<!-- =============================================================== -->

<!ELEMENT faqs (title?, authors?, (faq|faqsection)+)>
<!ATTLIST faqs %common.att;>

    <!ELEMENT faqsection (title, (faq | faqsection)+) >
    <!ATTLIST faqsection %common.att;>

    <!ELEMENT faq (question, answer)>
    <!ATTLIST faq %common.att;>

        <!ELEMENT question (%content.mix;|elaboration)*>
        <!ATTLIST question %common.att;>

        <!ELEMENT elaboration (%content.mix;)*>
        <!ATTLIST elaboration %common.att;>

        <!ELEMENT answer (%flow;)*>
        <!ATTLIST answer author IDREF #IMPLIED>

<!-- =============================================================== -->
<!-- End of DTD -->
<!-- =============================================================== -->
