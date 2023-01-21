package ai.prime.knowledge.neuron;

import ai.prime.knowledge.data.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Links {
    private Map<LinkType, Map<Data, Link>> links;

    public Links(){
        this.links = new HashMap<>();
    }

    public Collection<LinkType> getTypes(){
        return links.keySet();
    }

    public Collection<Link> getLinks(LinkType type){
        if (links.containsKey(type)){
            return Collections.unmodifiableCollection(links.get(type).values());
        }else{
            return Collections.EMPTY_SET;
        }
    }

    public boolean hasLink(Link link){
        return hasLink(link.getType(), link.getTo());
    }

    public boolean hasLink(LinkType type, Data to){
        Map<Data, Link> typeLinks = links.get(type);
        if (typeLinks==null)
            return false;

        return typeLinks.containsKey(to);
    }

    public void addLink(Link link){
        Map<Data, Link> typeLinks = links.get(link.getType());
        if (typeLinks==null){
            typeLinks = new HashMap<>();
            links.put(link.getType(), typeLinks);
        }

        typeLinks.put(link.getTo(), link);
    }
}
