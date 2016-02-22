# Jenkins Simple Healthcheck Plugin

A Jenkins plugin that can perform simple healthchecks against endpoints and report their status.
 
The status of each healthceck is written to an xUnit file after the build completes, allowing you to send notifications, track pass/failure history etc.

## Example setup

* Create a file name `healthcheck.json` containing the sample below and check it into your source code repository
* Add a new Jenkins job that checks out your repository
* Add a build step of 'Perform healthcheck' to your job and set the configuration file to `healthcheck.json`
* Add a post-build step of 'Publish JUnit test report' and set the pattern to `output/*.xml`
* Run the job

## Healthcheck configuration sample

    {
    	"targets": [
    		{
    			"name": "example website 1",
    			"url": "http://example.com",
    			"check": "HTTP_200"
    		},
    		{
    			"name": "example website 2",
    			"url": "http://localhost:30000",
    			"check": "HTTP_200"
    		}
    	]
    }

More examples are found in the `examples` directory.

# Author

Pete Cornish
