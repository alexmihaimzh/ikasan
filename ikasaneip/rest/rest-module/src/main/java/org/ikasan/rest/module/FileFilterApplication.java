package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * File Filter controller exposing CRUD to Message Filter entity
 */
@RequestMapping("/rest/filefilter")
@RestController
public class FileFilterApplication
{
    private static Logger logger = LoggerFactory.getLogger(FileFilterApplication.class);

    @Autowired
    private BaseFileTransferDao baseFileTransferDao;

    @Autowired
    private SystemEventService systemEventService;

    private ObjectMapper mapper;

    public FileFilterApplication() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule();
        this.mapper.registerModule(m);
    }

    @RequestMapping(
        method = RequestMethod.GET,
        value = "/search",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
        @RequestParam(value = "criteria", required = false) String criteria,
        @RequestParam(value = "clientId", required = false) String clientId
                             )
    {
        PagedSearchResult<FileFilter>  pagedResult = baseFileTransferDao.find(pageNumber, pageSize, criteria,clientId);
        return new ResponseEntity(pagedResult, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(@RequestParam(value = "id") Integer id)
    {
        FileFilter fileFilter = baseFileTransferDao.findById(id.intValue());
        return new ResponseEntity(fileFilter, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity delete(@RequestParam(value = "id") Integer id)
    {
        FileFilter fileFilter = baseFileTransferDao.findById(id.intValue());
        if ( fileFilter != null )
        {
            baseFileTransferDao.delete(fileFilter);
            try
            {
                String fileFilterJson = mapper.writeValueAsString(fileFilter);

                this.systemEventService.logSystemEvent(fileFilter.getClientId() +"_"+ fileFilter.getCriteria(),
                    String.format("File Filter deleted [%s]", fileFilterJson), UserUtil.getUser()
                                                      );
            }
            catch (JsonProcessingException e)
            {
                logger.warn("Issue converting file filter to json." + fileFilter, e);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(method = RequestMethod.POST,
                    value = "/",
                    consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity create(@RequestBody FileFilter fileFilter)
    {
        baseFileTransferDao.save(fileFilter);
        try
        {
            String fileFilterJson = mapper.writeValueAsString(fileFilter);

            this.systemEventService.logSystemEvent(fileFilter.getClientId() +"_"+ fileFilter.getCriteria(),
                String.format("File Filter created [%s]", fileFilterJson), UserUtil.getUser()
                                                  );
        }
        catch (JsonProcessingException e)
        {
            logger.warn("Issue converting file filter to json." + fileFilter, e);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    public void setBaseFileTransferDao(BaseFileTransferDao baseFileTransferDao)
    {
        this.baseFileTransferDao = baseFileTransferDao;
    }
}
