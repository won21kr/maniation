package de.kleppmann.maniation.geometry;

import java.util.Set;

import de.kleppmann.maniation.dynamics.Body;
import de.kleppmann.maniation.dynamics.GeneralizedBody;
import de.kleppmann.maniation.dynamics.MeshBody;
import de.kleppmann.maniation.maths.Quaternion;
import de.kleppmann.maniation.maths.Vector3D;
import de.kleppmann.maniation.scene.Bone;

public class ArticulatedLimb extends AnimateMesh {
    
    private MeshTriangle[] triangles;
    private Bone bone;
    private ArticulatedLimb parent;
    private CollisionVolume volume;
    private MeshBody dynamicBody;
    private Body.State dynamicState;
    private Vector3D baseRest, baseCurrent;
    private Quaternion orientRest, orientCurrent;
    
    public ArticulatedLimb(Set<MeshTriangle> triangles, Bone bone, ArticulatedLimb parent,
            ArticulatedMesh wholeMesh) {
        super(null);
        this.triangles = triangles.toArray(new MeshTriangle[triangles.size()]);
        this.bone = bone;
        this.parent = parent;
        this.volume = new CollisionVolume(this.triangles);
        // Determine rest position and orientation
        if (parent != null) {
            baseRest = parent.baseRest; orientRest = parent.orientRest;
            baseCurrent = parent.baseCurrent; orientCurrent = parent.orientCurrent;
        } else {
            baseRest = new Vector3D(); orientRest = new Quaternion();
            baseCurrent = wholeMesh.getCurrentLocation();
            orientCurrent = wholeMesh.getCurrentOrientation();
        }
        Vector3D local = new Vector3D(bone.getBase().getX(), bone.getBase().getY(), bone.getBase().getZ());
        baseRest = baseRest.add(orientRest.transform(local));
        orientRest = orientRest.mult(bone.getOrientation().getValue());
        baseCurrent = baseCurrent.add(orientCurrent.transform(local));
        orientCurrent = orientCurrent.mult(bone.getOrientation().getValue());
        if (bone.getPose() != null) orientCurrent = orientCurrent.mult(bone.getPose().getValue());
    }
    
    public Vector3D currentVertexPosition(Vector3D pos) {
        Vector3D local = orientRest.getInverse().transform(pos.subtract(baseRest));
        return orientCurrent.transform(local).add(baseCurrent);
    }

    @Override
    public MeshBody getDynamicBody() {
        return dynamicBody;
    }
    
    @Override
    public void setDynamicBody(GeneralizedBody dynamicBody) {
        this.dynamicBody = (MeshBody) dynamicBody;
        for (MeshTriangle tri : triangles) tri.setBody(this.dynamicBody);
    }
    
    @Override
    public Body.State getDynamicState() {
        return dynamicState;
    }
    
    @Override
    public void setDynamicState(GeneralizedBody.State state, Vector3D com) {
        if (!(state instanceof Body.State)) throw new IllegalArgumentException();
        com = dynamicBody.getCentreOfMass();
        this.dynamicState = (Body.State) state;
        this.orientCurrent = dynamicState.getOrientation();
        this.baseCurrent = dynamicState.getCoMPosition().subtract(
                orientCurrent.transform(com));
    }
    
    @Override
    public Vector3D getRestLocation() {
        return baseRest;
    }

    @Override
    public Quaternion getRestOrientation() {
        return orientRest;
    }

    @Override
    public Vector3D getCurrentLocation() {
        return baseCurrent;
    }

    @Override
    public Quaternion getCurrentOrientation() {
        return orientCurrent;
    }

    @Override
    public CollisionVolume getCollisionVolume() {
        return volume;
    }
    
    @Override
    public MeshTriangle[] getTriangles() {
        return triangles;
    }
    
    public Bone getBone() {
        return bone;
    }

    public ArticulatedLimb getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return orientCurrent.getW() + " " + orientCurrent.getX() + " " + 
                orientCurrent.getY() + " " + orientCurrent.getZ();
    }
}