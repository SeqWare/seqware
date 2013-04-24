        *** /usr/local/globus/default/setup/globus/sge.pm       2011-01-04 03:08:17.000000000 -0500
        --- /usr/local/globus/default/setup/globus/sge.pm.dist  2011-01-04 02:21:59.000000000 -0500
        *** 331,337 ****
              if($max_memory != 0)
              {
                  $self->log("Total max memory flag is set to $max_memory Mb");
        !         $sge_job_script->print("#\$ -l vf=$max_memory" . "M\n"); 
              }


        --- 331,337 ----
              if($max_memory != 0)
              {
                  $self->log("Total max memory flag is set to $max_memory Mb");
        !         $sge_job_script->print("#\$ -l h_data=$max_memory" . "M\n");
              }
