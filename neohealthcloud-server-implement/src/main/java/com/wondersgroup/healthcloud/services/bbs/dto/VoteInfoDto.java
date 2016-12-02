package com.wondersgroup.healthcloud.services.bbs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wondersgroup.healthcloud.jpa.entity.bbs.VoteItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ys on 2016/08/12.
 * @author ys
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteInfoDto {

    private Integer voteId;

    private List<VoteItemInfo> voteItems = new ArrayList<>();
    private Integer voteTotalCount;

    public void setVoteItems(List<VoteItem> voteItemsModels){
        if (voteItemsModels != null){
            for (VoteItem voteItemModel : voteItemsModels){
                this.voteItems.add(new VoteItemInfo(voteItemModel));
            }
        }
    }

    @Data
    public class VoteItemInfo{
        Integer itemId;
        String itemContent;
        Integer voteCount;
        public VoteItemInfo(VoteItem voteItemModel){
            this.itemId = voteItemModel.getId();
            this.itemContent = voteItemModel.getContent();
            this.voteCount = voteItemModel.getVoteCount();
        }
    }

}
