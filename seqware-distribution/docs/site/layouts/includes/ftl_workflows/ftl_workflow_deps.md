<pre><code>#!xml
&lt;!-- Part 2: list of control-flow dependencies --&gt;

  &lt;!-- Define task group dependencies --&gt;
&lt;#if using_helloworldexample_module &gt;
  &lt;child ref=&quot;ID001&quot;&gt;
    &lt;parent ref=&quot;IDPRE1&quot;/&gt;
  &lt;/child&gt;
  &lt;child ref=&quot;ID002&quot;&gt;
    &lt;parent ref=&quot;ID001&quot;/&gt;
  &lt;/child&gt;
  &lt;child ref=&quot;ID003&quot;&gt;
     &lt;parent ref=&quot;ID002&quot;/&gt;
   &lt;/child&gt;
  &lt;child ref=&quot;IDPOST1&quot;&gt;
    &lt;parent ref=&quot;ID003&quot;/&gt;
  &lt;/child&gt;
&lt;#else&gt;
  &lt;child ref=&quot;ID003&quot;&gt;
     &lt;parent ref=&quot;IDPRE1&quot;/&gt;
   &lt;/child&gt;
  &lt;child ref=&quot;IDPOST1&quot;&gt;
    &lt;parent ref=&quot;ID003&quot;/&gt;
  &lt;/child&gt;
&lt;/#if&gt;

&lt;!-- End of Dependencies --&gt;

&lt;/adag&gt;
</code></pre>
