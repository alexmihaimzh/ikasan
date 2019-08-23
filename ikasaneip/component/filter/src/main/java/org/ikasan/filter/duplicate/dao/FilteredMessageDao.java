/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */

package org.ikasan.filter.duplicate.dao;

import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.spec.search.PagedSearchResult;

import java.util.Date;
import java.util.List;

/**
 * DAO interface for interacting with filtered messages.
 * 
 * @author Ikasan Development Team
 *
 */
public interface FilteredMessageDao
{
    /**
     * Save new message.
     * @param message
     */
    void save(FilterEntry message);

    /**
     * Save or update a message.
     * @param message
     */
    void saveOrUpdate(FilterEntry message);

    /**
     * Try to find {@link FilterEntry} by its id: clientId and
     * criteria.
     *
     * @param message {@link FilterEntry} to be found
     *
     * @return The found {@link FilterEntry} or null if nothing
     *         found in persistence.
     */
    FilterEntry findMessage(FilterEntry message);

    /**
     * Try to find a List of{@link FilterEntry} by its id: clientId.
     *
     * @param clientId of {@link FilterEntry}s to be found
     *
     * @return The found List of {@link FilterEntry} or null if nothing
     *         found in persistence.
     */
    List<FilterEntry> findMessages(String clientId);

    /**
     * Find a Paged List of{@link FilterEntry} by its id: clientId or and clientId
     *
     * @param pageNo page number
     * @param pageSize page Size to be returned
     * @param criteria of {@link FilterEntry}s to be found
     * @param clientId of {@link FilterEntry}s to be found
     * @param fromDate from date criteria
     * @param untilDate until date criteria
     *
     * @return The found Paged Search result of {@link FilterEntry} or empty if nothing
     *         found in persistence.
     */
    PagedSearchResult<FilterEntry> findMessagesByPage(int pageNo, int pageSize,
        Integer criteria, String clientId, Date fromDate, Date untilDate);

    /**
     * Deletes given FilterEntry.
     * @param message to be deleted
     */
    void delete(FilterEntry message);

    /**
     * Delete expired Filter Entries from persistence 
     */
    void deleteAllExpired();

    /**
     * Allow batching of housekeep tasks to be turned on/off
     * @param batchedHousekeep
     */
    void setBatchHousekeepDelete(boolean batchedHousekeep);

    /**
     * Allow the batch size to be overridden
     * @param batchSize
     */
    void setHousekeepingBatchSize(int batchSize);

    /**
     * Allow the transaction batch size to be overridden
     * @param transactionBatchSize transactionBatchSize
     */
    void setTransactionBatchSize(int transactionBatchSize);

    /**
     * Checks if there are housekeepable items in existance, ie expired WiretapFlowEvents
     *
     * @return true if there is at least 1 expired WiretapFlowEvent
     */
    boolean housekeepablesExist();

    /**
     * Find expired messages.
     *
     * @return
     */
    List<FilterEntry> findExpiredMessages();

}
