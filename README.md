# OOCP
Object Oriented _Constraint_ Programming

Compiler from EMF.xmi and OCL.atl to CSP.choco

## API:
- Load(xmi)
- Load(xmi,atl)
- Solve()
- Save(xmi)

## 2 Compliers:
- XMI2Choco : which makes a Choco Model from an XMI file 
- OCL2Choco : which adds to the Choco Model from an OCL file

## .var() Annotation
You can annotate different models (.xcore, .xmi, .atl) to guide compilation
- src.var(prop, ...)
- choose between optimisation, fixing, exploration, etc..
- no annotations does model validation and simple completion
- limit the scope of the solver on large models


## Example Models
meta-model in xcore
```xcore
class Object {
  attrib : int
  ref : reference[0-5] Object
}
```
constraint meta-model in ocl
```
context Object : SubSetSum(target : int) =
  let query = self.var(ref).ref.ref.attribute in
    query.sum() = target and
    query.isUnique();
```
model to solve for, can be programatically generated using .xcore
```xmi
<object 1>
  <attribute> 10 </attribute>
</object>
<object 2>
  <attribute> 11 </attribute>
</object>
<object 3>
  <attribute> 12 </attribute>
</object>
<object 4>
  <attribute> 13 </attribute>
</ object>
<int target = 33 />
```
from this we make a constraint model to solve
this is also a bit pseudocodey for now
[See navCSP_SubSetSum for details](https://github.com/ArtemisLemon/navCSP_SubsetSum)


## Quick Links for AIMT Meetings
- [OCL2Choco](https://github.com/ArtemisLemon/OOCP/blob/master/lib/src/main/java/org/uml2choco/atlocl2choco/OCL2Choco.java)
- [XMI2Choco](https://github.com/ArtemisLemon/OOCP/blob/master/lib/src/main/java/org/uml2choco/xmi2choco/XMI2Choco.java)
- [OCLinChoco](https://github.com/ArtemisLemon/OCLinChoco)
- [OCLinChoco:RefTable](https://github.com/ArtemisLemon/OCLinChoco/blob/master/lib/src/main/java/org/oclinchoco/ReferenceTable.java)
[OCLinChoco:NavCSP](https://github.com/ArtemisLemon/OCLinChoco/blob/master/lib/src/main/java/org/oclinchoco/NavCSP.java)
- [OCL 2.4](https://www.omg.org/spec/OCL/2.4/)
- [ATL lang](https://wiki.eclipse.org/ATL/User_Guide_-_The_ATL_Language)
- [ATL API](https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.m2m.atl.doc%2Fguide%2Fdeveloper%2FATL+Developer+Guide.html)
- [Ecore API](https://download.eclipse.org/modeling/emf/emf/javadoc/2.11/org/eclipse/emf/ecore/package-summary.html)
- [Var&navCSP overleaf](https://www.overleaf.com/project/66d81a0bd3edfa84f15b717b)
