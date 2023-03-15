package es.ucm.fdi.acweb.model;

import es.ucm.fdi.ac.SourceSet;
import es.ucm.fdi.ac.extract.FileTreeNode;
import es.ucm.fdi.acweb.LocalData;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class FileTreeNodeWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private Long id;

    private String path; // relative to analysis path

    @OneToOne
    private SourceSetWeb ss;

    @ManyToOne
    private FileTreeNodeWeb parent;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="parent_id")
    private List<FileTreeNodeWeb> children = new ArrayList<>();

    public FileTreeNode ftnToAc(){
        return new FileTreeNode(new File(this.path), this.parent.ftnToAc());
    }

    public static FileTreeNodeWeb ftnFromAc(FileTreeNode fileTreeNode, SourceSetWeb sourceSetWeb, FileTreeNodeWeb parent){
        FileTreeNodeWeb fileTreeNodeWeb = new FileTreeNodeWeb();

        //Persistimos los childen
        for(FileTreeNode i : fileTreeNode.getChildren()){
           fileTreeNodeWeb.getChildren().add(ftnFromAc(i, sourceSetWeb, fileTreeNodeWeb));
        }
        //Persistimos otros valores
        fileTreeNodeWeb.setPath(fileTreeNode.getPath());
        fileTreeNodeWeb.setSs(sourceSetWeb);
        fileTreeNodeWeb.setParent(parent);

        return fileTreeNodeWeb;
    }

}
