require 'pp'
require 'yajl/json_gem'
require 'stringio'

module GitHub
  module Resources
    module Helpers
      STATUSES = {
        200 => '200 OK',
        201 => '201 Created',
        202 => '202 Accepted',
        204 => '204 No Content',
        301 => '301 Moved Permanently',
        304 => '304 Not Modified',
        401 => '401 Unauthorized',
        403 => '403 Forbidden',
        404 => '404 Not Found',
        409 => '409 Conflict',
        422 => '422 Unprocessable Entity',
        500 => '500 Server Error',
          0 => '' # Use this for a request header.
      }

      def headers(status, head = {})
        css_class = (status == 204 || status == 404) ? 'headers no-response' : 'headers'
        lines = ["Status: #{STATUSES[status]}"]
        if status == 0
          lines = [] # Don't want to display "Status:" for request headers.
        end
        head.each do |key, value|
          case key
            when :pagination
              lines << 'Link: <https://api.github.com/resource?page=2>; rel="next",'
              lines << '      <https://api.github.com/resource?page=5>; rel="last"'
            else lines << "#{key}: #{value}"
          end
        end

        # lines << "X-RateLimit-Limit: 5000"
        # lines << "X-RateLimit-Remaining: 4999"

        %(<pre class="#{css_class}"><code>#{lines * "\n"}</code></pre>\n)
      end

      def json(key)
        hash = case key
          when Hash
            h = {}
            key.each { |k, v| h[k.to_s] = v }
            h
          when Array
            key
          else Resources.const_get(key.to_s.upcase)
        end

        hash = yield hash if block_given?

        %(<pre class="highlight"><code class="language-javascript">) +
          JSON.pretty_generate(hash) + "</code></pre>"
      end
    end

    OWNER = {
      :email => "john.hurt@oicr.on.ca",
      :first_name => "John",
      :last_name => "Hurt",
      :institution => "Ontario Institute for Cancer Research"
    }
    ORGANISM = {
      :name => "Homo sapiens",
      :code => "Homo_sapiens",
      :ncbi_taxonomy_id => 9606
    }
    ATTR1 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_reaction_id",
      :value => "3795"
    }
    ATTR2 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_library_source_template_type",
      :value => "EX"
    }
    ATTR3 = {
      :entity_url =>  "http://localhost:8888/seqware-webservice/samples/1547",
      :name => "geo_template_id",
      :value => "5788"
    }
    ATTR4 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/1549",
      :name => "geo_template_type",
      :value => "Illumina PE Library"
    }
    ATTR5 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_template_id",
      :value => "6143"
    }
    ATTR6 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/1547",
      :name => "geo_template_type",
      :value => "gDNA"
    }
    ATTR7 = {
      :entity_url =>  "http://localhost:8888/seqware-webservice/samples/1548",
      :name => "geo_template_type",
      :value => "Illumina PE Library"
    }
    ATTR8 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/1548",
      :name => "geo_template_id",
      :value => "6141"
    }
    ATTR9 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_template_type",
      :value => "Illumina PE Libary Seq"
    }
    ATTR10 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_tissue_type",
      :value => "P"
    }
    ATTR11 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/1549", 
      :name => "geo_template_id",
      :value => "6142"
    }
    ATTR12 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_targeted_resequencing",
      :value => "Agilent SureSelect ICGC/Sanger Exon"
    }
    ATTR13 = {
      :entity_url => "http://localhost:8888/seqware-webservice/samples/234",
      :name => "geo_tissue_origin",
      :value => "Pa"
    }
    LIBRARY = {
      :url => "http://localhost:8888/seqware-webservice/x/library/234",
      :name => "FPS_0014_Pa_P_PE_300_EX",
      :description => "Sample ID: RT-28734",
      :create_time_stamp => "2010-12-07T13:18:30-05:00",
      :update_time_stamp => "2011-03-18T16:56:44-04:00",
      :owner => OWNER,
      :organism => ORGANISM,
      :attributes => [ATTR1,ATTR2,ATTR3,ATTR4,ATTR5,ATTR6,ATTR7,ATTR8,ATTR9,ATTR10,ATTR11,ATTR12,ATTR13],
      :parent_urls => ["http://localhost:8888/seqware-webservice/samples/1549"]
    }
  end
end

include GitHub::Resources::Helpers
