package pt.iknow.floweditor;

import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;


// TODO Pretendo extender JFrame e apresentar a tabela com o catalogo de variaveis
public class CatalogListFrame extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = -5401848332963797223L;
  
  public CatalogListFrame() {
    initComponent();
  }
  
  private JTree catalogTree;
  private void initComponent() {
    
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    JScrollPane treeScrollPane = new JScrollPane();
    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    
    Desenho d = null;
    Collection<Atributo> catalogue = d.getCatalogue();
    
    for(Atributo a : catalogue) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(a);
      root.add(node);
    }
    
    
    
    catalogTree = new JTree(root);

    setLayout(new java.awt.BorderLayout());

    treeScrollPane.setViewportView(catalogTree);

    add(treeScrollPane, java.awt.BorderLayout.CENTER);

  }
  

}
