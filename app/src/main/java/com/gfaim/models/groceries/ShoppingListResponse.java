package com.gfaim.models.groceries;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingListResponse implements Serializable {

    @SerializedName("family_id")
    private Long familyId;

    @SerializedName("items")
    private List<ShoppingItem> items;

    // Méthode d'aide pour rendre le débogage plus facile
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShoppingListResponse{");
        sb.append("familyId=").append(familyId);
        sb.append(", items=");
        if (items == null) {
            sb.append("null");
        } else {
            sb.append("[size=").append(items.size());
            if (!items.isEmpty()) {
                sb.append(", first=").append(items.get(0));
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }
}
