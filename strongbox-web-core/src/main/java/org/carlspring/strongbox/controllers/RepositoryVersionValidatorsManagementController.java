package org.carlspring.strongbox.controllers;

import org.carlspring.strongbox.configuration.ConfigurationManager;
import org.carlspring.strongbox.storage.repository.Repository;
import org.carlspring.strongbox.storage.repository.VersionValidatorType;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Przemyslaw Fusik
 */
@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping(value = "/configuration/repositories/version-validators")
@Api(value = "/configuration/repositories/version-validators")
public class RepositoryVersionValidatorsManagementController
        extends BaseController
{

    @Inject
    private ConfigurationManager configurationManager;

    @ApiOperation(value = "Enumerates all version validators of the requested repository")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "All version validators of the requested repository"),
                            @ApiResponse(code = 404, message = "Repository not found") })
    @RequestMapping(value = "/{storageId}/{repositoryId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE,
                                                                                                    MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity list(@PathVariable String storageId,
                               @PathVariable String repositoryId)
    {
        Repository repository = null;
        try
        {
            repository = configurationManager.getRepository(storageId, repositoryId);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }

        Set<String> versionValidators = new TreeSet<>(repository.getArtifactCoordinateValidators()
                                                                .stream().map(v -> v.toString())
                                                                .collect(Collectors.toSet()));

        return repository == null ? ResponseEntity.notFound().build() :
               ResponseEntity.ok(versionValidators);
    }

    @ApiOperation(value = "Adds version validator type to the requested repository")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operation success"),
                            @ApiResponse(code = 404, message = "Repository not found") })
    @RequestMapping(value = "/{storageId}/{repositoryId}/{versionValidatorType}",
                    method = RequestMethod.PUT,
                    produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity add(@PathVariable String storageId,
                              @PathVariable String repositoryId,
                              @PathVariable VersionValidatorType versionValidatorType)
    {
        Repository repository = null;
        try
        {
            repository = configurationManager.getRepository(storageId, repositoryId);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }

        if (repository == null)
        {
            return ResponseEntity.notFound().build();
        }

        repository.getArtifactCoordinateValidators().add(versionValidatorType.name());

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Deletes version validator type from the requested repository")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Operation success"),
                            @ApiResponse(code = 404, message = "Repository not found") })
    @RequestMapping(value = "/{storageId}/{repositoryId}/{versionValidatorType}",
                    method = RequestMethod.DELETE,
                    produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity delete(@PathVariable String storageId,
                                 @PathVariable String repositoryId,
                                 @PathVariable VersionValidatorType versionValidatorType)
    {
        Repository repository = null;
        try
        {
            repository = configurationManager.getRepository(storageId, repositoryId);
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }

        if (repository == null)
        {
            return ResponseEntity.notFound().build();
        }

        repository.getArtifactCoordinateValidators().remove(versionValidatorType);

        return ResponseEntity.ok().build();
    }

}
