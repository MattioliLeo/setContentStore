# Custom Content Store Behavior for Alfresco

In large-scale Alfresco installations, it’s common to use multiple ContentStores. According to Alfresco's official documentation, the `cm:storeName` aspect allows you to designate a specific ContentStore for storing a node. 

In one scenario, we needed all content within a particular Site/Library to be stored in a secondary ContentStore. We initially implemented a folder rule to set the `cm:storeName` aspect at node creation. However, this approach caused a performance issue: the node would first be loaded into the primary ContentStore, then offloaded into `temp/Alfresco`, and finally reloaded into the secondary ContentStore.

To solve this, we developed a custom Behavior that sets the `cm:storeName` aspect at the start of the node creation transaction. This eliminates the need to load the content into the primary ContentStore, improving performance.

> **Note:** This code is provided as an example only. It is not intended for use in production environments but rather as a starting point for adding custom logic or filters.

## Usage

This project demonstrates a Behavior that configures `cm:storeName` before node creation. You can use this as a foundation for additional custom logic or adjustments.

