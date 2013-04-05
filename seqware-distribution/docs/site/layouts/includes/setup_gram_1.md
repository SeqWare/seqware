        *** /usr/local/globus/default/setup/globus/sge.pm       2011-01-04 02:26:09.000000000 -0500
        --- /usr/local/globus/default/setup/globus/sge.pm.dist  2011-01-04 02:21:59.000000000 -0500
        ***************
        *** 47,55 ****
              $mpi_pe      = '';
              #
              if(($mpirun eq "no") && ($sun_mprun eq "no"))
        !       { $supported_job_types = "(single|multiple|condor)"; }
              else
        !       { $supported_job_types = "(mpi|single|multiple|condor)"; }
              #
              $cat         = '/bin/cat';
              #
        --- 47,55 ----
              $mpi_pe      = '';
              #
              if(($mpirun eq "no") && ($sun_mprun eq "no"))
        !       { $supported_job_types = "(single|multiple)"; }
              else
        !       { $supported_job_types = "(mpi|single|multiple)"; }
              #
              $cat         = '/bin/cat';
              #
        ***************
        *** 577,588 ****
                                             . $description->stdin() . "\n");
                  }
              }
        -     elsif($description->jobtype() eq "condor" && $description->count() > 1 ) 
        -     {
        -       # NOTE: you may need to change 'serial' to the parallel environment name used on your cluster
        -       $sge_job_script->print("#\$ -pe serial "
        -                                    . $description->count() ."\n"
        -                                    . $executable . " $args < ". $stdin
        -                                    . "\n");
        -        if (!$rc)
        -        {
        -            return $self->respond_with_failure_extension(
        -                    "print: $sge_job_script_name: $!",
        -                    Globus::GRAM::Error::TEMP_SCRIPT_FILE_FAILED());
        -        }
        -     }
              elsif($description->jobtype() eq "multiple")
              {
                  #####
        --- 577,582 ----

